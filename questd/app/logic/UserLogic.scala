package logic

import java.util.Date
import scala.util.Random
import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime
import models.domain._
import models.domain.base._
import models.domain.ContentType._
import controllers.domain.app.protocol.ProfileModificationResult._
import components.componentregistry.ComponentRegistrySingleton
import functions._
import constants._
import play.Logger
import controllers.domain.admin._
import com.mongodb.BasicDBList
import models.store.dao.ThemeDAO
import controllers.domain.OkApiResult

// This should not go to DB directly since API may have cache layer.
class UserLogic(val user: User) {

  lazy val api = ComponentRegistrySingleton.api

  /**
   * **************************
   * Rights
   * **************************
   */

  def calculateRights: Rights = {
    Rights(
      unlockedFunctionality = restrictions.foldLeft(Set[String]()) { case (c, (right, level)) => if (level <= user.profile.publicProfile.level) c + right else c },
      maxFriendsCount = maxNumberOfFriendsOnLevel(user.profile.publicProfile.level))
  }

  /**
   * **************************
   * Proposing quests.
   * **************************
   */

  /**
   * Check is the user can purchase quest proposals.
   */
  def canPurchaseQuestProposals = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoQuests.toString()))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costOfPurchasingQuestProposal))
      NotEnoughAssets
    else if (user.profile.questProposalContext.questProposalCooldown.after(new Date()))
      CoolDown
    else if (user.profile.questProposalContext.takenTheme != None)
      InvalidState
    else
      OK
  }

  /**
   * Is user can propose quest of given type.
   */
  def canTakeQuestTheme = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoQuests.toString()))
      NotEnoughRights
    else if (user.profile.questProposalContext.purchasedTheme == None)
      InvalidState
    else if (!(user.profile.assets canAfford costOfTakingQuestTheme))
      NotEnoughAssets
    else
      OK
  }

  /**
   * Is user can propose quest of given type.
   */
  def canProposeQuest(conentType: ContentType) = {
    val content = conentType match {
      case Photo => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoQuests.toString())
      case Video => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitVideoQuests.toString())
    }

    if (!content)
      NotEnoughRights
    else if (user.profile.questProposalContext.takenTheme == None)
      InvalidState
    else
      OK
  }

  /**
   * Tells cost of next theme purchase
   */
  def costOfPurchasingQuestProposal = {
    if (user.profile.questProposalContext.numberOfPurchasedThemes < numberOfThemesSkipsForCoins) {
      val c = costToSkipProposal(user.profile.publicProfile.level, user.profile.questProposalContext.numberOfPurchasedThemes + 1)
      Assets(coins = c)
    } else {
      Assets(money = 1)
    }
  }

  /**
   * Cost of proposing quest.
   */
  def costOfTakingQuestTheme = {
    Assets(coins = costToTakeQuestTheme(user.profile.publicProfile.level))
  }

  /**
   * Select theme for the user to take.
   * TODO: test me.
   */
  def getRandomThemeForQuestProposal(themesCount: Long): Option[Theme] = {

    def themeFromGlobal = {
      val themes = api.allThemes(AllThemesRequest(sorted = true)).body.get.themes

      // TODO pass here list of themes we reviewed today.
      util.selectTheme(themes, List())
    }

    val probabilityOfRecentList = {
      val maxShare = api.config(api.ConfigParams.FavoriteThemesShare).toDouble
      val minShare = maxShare / 4
      val ourShare = user.history.selectedThemeIds.length / themesCount.toDouble

      if (minShare > ourShare)
        0
      else
        Math.min(1, (ourShare - minShare) / (maxShare - minShare)) * api.config(api.ConfigParams.FavoriteThemesProbability).toDouble
    }
    
    Logger.debug("Probability of recent list " + probabilityOfRecentList)

    val rand = new Random(System.currentTimeMillis())
    if (rand.nextDouble < probabilityOfRecentList) {
      // Use recent list
      Logger.debug("Using recent list")
      val id = user.history.selectedThemeIds(rand.nextInt(user.history.selectedThemeIds.length))
      
      Logger.trace("  Selected id from themes in history: " + id)

      // TODO: check here it's not in today's list of themes
      if (List().contains(id)) {
        Logger.debug("Recent list returned theme we've used today, requesting from global one.")
        themeFromGlobal
      } else {
        api.getTheme(GetThemeRequest(id)) match {
          case OkApiResult(Some(GetThemeResult(theme))) => Some(theme)
          case _ => None
        }
      }
    } else {
      // Use global list.
      Logger.debug("Using global list")
      themeFromGlobal
    }

    None
  }

  /**
   *
   */
  def getCooldownForTakeTheme: Date = {
    import com.github.nscala_time.time.Imports._
    import org.joda.time.DateTime

    val daysToSkipt = questProposalPeriod(user.profile.publicProfile.level)

    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    (DateTime.now(tz) + daysToSkipt.days).hour(constants.flipHour).minute(0).second(0) toDate ()
  }

  /**
   * Is user can give up quest proposal.
   */
  def canGiveUpQuestProposal = {
    if (user.profile.questProposalContext.takenTheme == None)
      InvalidState
    else
      OK
  }

  /**
   * Reward for approving quest.
   */
  def rewardForMakingApprovedQuest = {
    Assets(rating = ratingForProposalAtLevel(user.profile.publicProfile.level))
  }

  def penaltyForCheatingQuest = {
    (rewardForMakingApprovedQuest * questProposalCheatingPenalty) clampTop user.profile.assets
  }

  def penaltyForIACQuest = {
    (rewardForMakingApprovedQuest * questProposalIACPenalty) clampTop user.profile.assets
  }

  /**
   *
   */
  def costOfGivingUpQuestProposal = {
    Assets(rating = ratingToGiveUpQuestProposal(user.profile.publicProfile.level)) clampTop user.profile.assets
  }

  /**
   * Check is quest deadline passed and quest should be autogave up.
   */
  def proposalDeadlineReached = {
    ((user.profile.questProposalContext.takenTheme != None)
      && (user.profile.questProposalContext.questProposalCooldown.before(new Date())))
  }

  /**
   * **********************************
   * Solving quests
   * **********************************
   */

  /**
   * Check can the user purchase quest.
   */
  def canPurchaseQuest = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoResults.toString()))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costOfPurchasingQuest))
      NotEnoughAssets
    else if (user.profile.questSolutionContext.questCooldown.after(new Date()))
      CoolDown
    else if (user.profile.questSolutionContext.takenQuest != None)
      InvalidState
    else
      OK
  }

  /**
   * Tells cost of next theme purchase
   */
  def costOfPurchasingQuest = {
    if (user.profile.questSolutionContext.numberOfPurchasedQuests < numberOfQuestsSkipsForCoins) {

      val questDuration = user.profile.questSolutionContext.purchasedQuest match {
        case Some(QuestInfoWithID(_, q)) => q.daysDuration
        case _ => 1
      }

      val c = costToSkipQuest(user.profile.publicProfile.level, user.profile.questSolutionContext.numberOfPurchasedQuests + 1, questDuration)
      Assets(coins = c)
    } else {
      Assets(money = 1)
    }
  }

  /**
   * Takes everything into account and returns possible quest to be solved by user.
   */
  def getRandomQuestForSolution: Option[Quest] = {
    val quests = api.allQuestsInRotation(AllQuestsRequest(user.profile.publicProfile.level - questLevelToleranceDown, user.profile.publicProfile.level + questLevelToleranceUp)).body.get.quests

    util.selectQuest[Quest](quests, (_.id), (_.authorUserID), user.history.solvedQuestIds)
  }

  /**
   * Check are we able to take quest.
   */
  def canTakeQuest = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoResults.toString()))
      NotEnoughRights
    else if (user.profile.questSolutionContext.purchasedQuest == None)
      InvalidState
    else if (!(user.profile.assets canAfford costOfTakingQuest))
      NotEnoughAssets
    else
      OK
  }

  /**
   * Get cost of taking quest to resolve.
   */
  def costOfTakingQuest = {
    Assets(coins = costToTakeQuestToSolve(user.profile.publicProfile.level, purchasedQuestDuration))
  }

  /**
   * Is user can propose quest of given type.
   */
  def canResolveQuest(conentType: ContentType) = {
    val content = conentType match {
      case Photo => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoResults.toString())
      case Video => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitVideoResults.toString())
    }

    if (!content)
      NotEnoughRights
    else if (user.profile.questSolutionContext.takenQuest == None)
      InvalidState
    else
      OK
  }

  /**
   * Is user can give up quest.
   */
  def canGiveUpQuest = {
    if (user.profile.questSolutionContext.takenQuest == None)
      InvalidState
    else
      OK
  }

  /**
   * How much it'll cost to give up taken quest.
   */
  def costOfGivingUpQuest = {
    Assets(rating = ratingToGiveUpQuest(user.profile.publicProfile.level, takenQuestDuration)) clampTop user.profile.assets
  }

  /**
   * Cooldown for taking quest.
   */
  def getCooldownForTakeQuest(qi: QuestInfo) = {
    val daysToSkipt = qi.daysDuration

    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    (DateTime.now(tz) + daysToSkipt.days).hour(constants.flipHour).minute(0).second(0) toDate ()
  }

  /**
   * Time to solve quest.
   */
  def getDeadlineForTakeQuest(qi: QuestInfo) = {
    val minutesToSolveQuest = qi.minutesDuration

    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    (DateTime.now(tz) + minutesToSolveQuest.minutes) toDate ()
  }

  /**
   * Cooldown for reseting purchases. Purchases should be reset in nearest 5am at user's time.
   */
  def getResetPurchasesTimeout = {
    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    (DateTime.now(tz) + 1.day).hour(constants.flipHour).minute(0).second(0) toDate ()
  }

  /**
   * Reward for lost quest.
   */
  def rewardForLosingQuest(quest: Quest) = {
    Assets(rating = ratingToLoseQuest(user.profile.publicProfile.level, quest.info.daysDuration))
  }

  /**
   * Reward for won quest.
   */
  def rewardForWinningQuest(quest: Quest) = {
    Assets(rating = ratingToWinQuest(user.profile.publicProfile.level, quest.info.daysDuration))
  }

  /**
   * Penalty for cheating solution
   */
  def penaltyForCheatingSolution(quest: Quest) = {
    (rewardForLosingQuest(quest) * questSolutionCheatingPenalty) clampTop user.profile.assets
  }

  /**
   * Penalty for IAC solution
   */
  def penaltyForIACSolution(quest: Quest) = {
    (rewardForLosingQuest(quest) * questSolutionIACPenalty) clampTop user.profile.assets
  }

  /**
   * Returns taken quest duration in days.
   */
  private def takenQuestDuration = {
    questDuration(user.profile.questSolutionContext.takenQuest)
  }

  /**
   * Returns purchased quest duration in days.
   */
  private def purchasedQuestDuration = {
    questDuration(user.profile.questSolutionContext.purchasedQuest)
  }

  private def questDuration(q: Option[QuestInfoWithID]) = {
    q match {
      case Some(QuestInfoWithID(_, i)) => i.daysDuration
      case None => 0
    }
  }

  /**
   * Check is quest deadline passed and quest should be autogave up.
   */
  def questDeadlineReached = {
    ((user.profile.questSolutionContext.takenQuest != None)
      && (user.profile.questSolutionContext.questDeadline.before(new Date())))
  }

  /**
   * ********************
   * Vote quest proposal
   * ********************
   */

  /**
   *
   */
  def canGetQuestProposalForVote = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuestProposals.toString()))
      NotEnoughRights
    else if (user.profile.questProposalVoteContext.reviewingQuest != None)
      InvalidState
    else
      OK
  }

  /**
   *
   */
  def canVoteQuestProposal = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuestProposals.toString()))
      NotEnoughRights
    else if (user.profile.questProposalVoteContext.reviewingQuest == None)
      InvalidState
    else
      OK
  }

  /**
   * @return None if no more quests to vote for today.
   */
  def getQuestProposalToVote: Option[Quest] = {
    val quests = api.allQuestsOnVoting.body.get.quests

    util.selectQuest[Quest](quests, (_.id), (_.authorUserID), user.history.votedQuestProposalIds)
  }

  /**
   * Reward for voting for quest proposal.
   */
  def getQuestProposalVoteReward = {
    val level = user.profile.publicProfile.level
    val count = user.profile.questProposalVoteContext.numberOfReviewedQuests

    if (count < rewardedProposalVotesPerLevel(level))
      (Assets(coins = rewardForVotingProposal(level, count + 1))).clampBot
    else
      Assets()
  }

  /**
   * ********************
   * Vote quest solution
   * ********************
   */

  /**
   *
   */
  def canGetQuestSolutionForVote = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuestSolutions.toString()))
      NotEnoughRights
    else if (user.profile.questSolutionVoteContext.reviewingQuestSolution != None)
      InvalidState
    else
      OK
  }

  /**
   * @return None if no more quests to vote for today.
   */
  def getQuestSolutionToVote: Option[QuestSolution] = {
    val solutions = api.allQuestSolutionsOnVoting(
      AllQuestSolutionsRequest(user.profile.publicProfile.level - constants.solutionLevelDownTolerance, user.profile.publicProfile.level + constants.solutionLevelUpTolerance)).body.get.quests

    util.selectQuest[QuestSolution](solutions, (_.id), (_.userID), user.history.votedQuestSolutionIds)
  }

  /**
   *
   */
  def canVoteQuestSolution = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuestSolutions.toString()))
      NotEnoughRights
    else if (user.profile.questSolutionVoteContext.reviewingQuestSolution == None)
      InvalidState
    else
      OK
  }

  /**
   * Reward for quest solution.
   */
  def getQuestSolutionVoteReward = {
    val level = user.profile.publicProfile.level
    val count = user.profile.questSolutionVoteContext.numberOfReviewedSolutions

    if (count < rewardedSolutionVotesPerLevel(level))
      Assets(coins = rewardForVotingSolution(level, count + 1)).clampBot
    else
      Assets()
  }

  /**
   * ***********************
   * Daily results
   * ***********************
   */
  def getStartOfCurrentDailyResultPeriod: Date = {
    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    DateTime.now(tz).hour(constants.flipHour).minute(0).second(0) toDate ()
  }

  /**
   * Tells cost of next theme purchase
   */
  def dailyAssetsDecrease = {
    Assets(rating = dailyRatingDecrease(user.profile.publicProfile.level)) clampTop (user.profile.assets)
  }

  /**
   * ***********************
   * Leveling up
   * ***********************
   */
  def ratingToNextLevel: Int = {
    ratToGainLevel(user.profile.publicProfile.level + 1)
  }

  /**
   * *******************
   * Shortlist
   * *******************
   */

  def canShortlist = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.AddToShortList.toString()))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToShortlist))
      NotEnoughAssets
    else
      OK
  }

  def costToShortlist = {
    Assets(coins = costToShortlistPerson(user.profile.publicProfile.level))
  }

  /**
   * ********************
   * Friends
   * ********************
   */

  def canAddFriend(potentialFriend: User) = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.InviteFriends.toString()))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToAddFriend(potentialFriend)))
      NotEnoughAssets
    else if (user.friends.length >= user.profile.rights.maxFriendsCount)
      LimitExceeded
    else
      OK
  }

  def costToAddFriend(potentialFriend: User) = {
    val friendAhead = potentialFriend.profile.publicProfile.level - user.profile.publicProfile.level

    if (friendAhead > 0) {
      Assets(money = friendAhead)
    } else {
      Assets(coins = costToInviteFriend(user.profile.publicProfile.level, -friendAhead))
    }
  }

  /**
   * ********************
   * Friends
   * ********************
   */
  def userActive = {
    val activeDays = api.config(api.ConfigParams.ActiveUserDays).toInt

    user.auth.lastLogin match {
      case None => false
      case Some(d) => {
        (new DateTime(d) + activeDays.days) > (DateTime.now)
      }
    }
  }

  /**
   * *******************************************
   * Utils for user logic
   * *******************************************
   */
  private object util {

    /**
     * Check is string in list of dblists of strings.
     */
    def listOfListsContainsString(l: List[List[String]], s: String) = {
      if (l.length > 0) {

        // This is required since salat makes embedded lists as BasicDBLists.
        val rv = if (l.head.getClass() == classOf[BasicDBList]) {
          for (
            out <- l.asInstanceOf[List[BasicDBList]];
            in <- out.toArray();
            if in == s
          ) yield {
            true
          }
        } else {
          for (
            out <- l;
            in <- out;
            if in == s
          ) yield {
            true
          }
        }

        rv.length > 0

      } else {
        false
      }
    }

    /**
     * Select quest what is not or quest and not in given list.
     */
    private[logic] def selectQuest[T](i: Iterator[T], fid: (T => String), fauthorid: (T => String), usedQuests: List[List[String]]): Option[T] = {
      if (i.hasNext) {
        val q = i.next()

        if (fauthorid(q) != user.id
          && !(util.listOfListsContainsString(usedQuests, fid(q)))) {
          Some(q)
        } else {
          selectQuest(i, fid, fauthorid, usedQuests)
        }
      } else {
        None
      }
    }

    /**
     * Select theme from returned iterator what is not contained in given list.
     */
    private[logic] def selectTheme(i: Iterator[Theme], usedThemes: List[Theme]): Option[Theme] = {
      if (i.hasNext) {
        val t = i.next()

        if (!usedThemes.contains(t.id)) {
          Logger.trace("  Theme selected: " + t.id)
          Some(t)
        } else {
          selectTheme(i, usedThemes)
        }
      } else {
        None
      }
    }

  }

}

