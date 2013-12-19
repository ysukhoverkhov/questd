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

// This should not go to DB directly since API may have cache layer.
class UserLogic(val user: User) {

  lazy val api = ComponentRegistrySingleton.api

  /**
   * **************************
   * Proposing quests.
   * **************************
   */

  /**
   * Check is the user can purchase quest proposals.
   */
  def canPurchaseQuestProposals = {
    if (user.profile.rights.submitPhotoQuests > user.profile.level)
      LevelTooLow
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
    if (user.profile.rights.submitPhotoQuests > user.profile.level)
      LevelTooLow
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
    val level = conentType match {
      case Photo => user.profile.rights.submitPhotoQuests
      case Video => user.profile.rights.submitVideoQuests
    }

    if (level > user.profile.level)
      LevelTooLow
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
      val c = costToSkipProposal(user.profile.level, user.profile.questProposalContext.numberOfPurchasedThemes + 1)
      Assets(coins = c)
    } else {
      Assets(money = 1)
    }
  }

  /**
   * Cost of proposing quest.
   */
  def costOfTakingQuestTheme = {
    Assets(coins = costToTakeQuestTheme(user.profile.level))
  }

  /**
   * Select theme for the user to take.
   */
  def getRandomThemeForQuestProposal = {
    val themes = api.allThemes.body.get.themes

    if (themes.size > 0) {
      val rand = new Random(System.currentTimeMillis())
      val random_index = rand.nextInt(themes.length)
      themes(random_index)
    } else {
      Logger.error("No themes in database, stub theme returned")
      Theme()
    }
  }

  /**
   *
   */
  def getCooldownForTakeTheme: Date = {
    import com.github.nscala_time.time.Imports._
    import org.joda.time.DateTime

    val daysToSkipt = questProposalPeriod(user.profile.level)

    val tz = DateTimeZone.forOffsetHours(user.profile.bio.timezone)
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
    Assets(rating = ratingForProposalAtLevel(user.profile.level))
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
    Assets(rating = ratingToGiveUpQuestProposal(user.profile.level)) clampTop user.profile.assets
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
    if (user.profile.rights.submitPhotoResults > user.profile.level)
      LevelTooLow
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

      val c = costToSkipQuest(user.profile.level, user.profile.questSolutionContext.numberOfPurchasedQuests + 1, questDuration)
      Assets(coins = c)
    } else {
      Assets(money = 1)
    }
  }

  /**
   * Takes everything into account and returns possible quest to be solved by user.
   */
  def getRandomQuestForSolution: Option[Quest] = {
    val quests = api.allQuestsInRotation(AllQuestsRequest(user.profile.level - questLevelTolerance, user.profile.level + questLevelTolerance)).body.get.quests

    if (quests.length == 0) {
      None
    } else {
      val rand = new Random(System.currentTimeMillis())
      val random_index = rand.nextInt(quests.length)
      Some(quests(random_index))
    }
  }

  /**
   * Check are we able to take quest.
   */
  def canTakeQuest = {
    if (user.profile.rights.submitPhotoResults > user.profile.level)
      LevelTooLow
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
    Assets(coins = costToTakeQuestToSolve(user.profile.level, purchasedQuestDuration))
  }

  /**
   * Is user can propose quest of given type.
   */
  def canResulveQuest(conentType: ContentType) = {
    val level = conentType match {
      case Photo => user.profile.rights.submitPhotoResults
      case Video => user.profile.rights.submitVideoResults
    }

    if (level > user.profile.level)
      LevelTooLow
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
    Assets(rating = ratingToGiveUpQuest(user.profile.level, takenQuestDuration)) clampTop user.profile.assets
  }

  /**
   * Cooldown for taking quest.
   */
  def getCooldownForTakeQuest(qi: QuestInfo) = {
    val daysToSkipt = qi.daysDuration

    val tz = DateTimeZone.forOffsetHours(user.profile.bio.timezone)
    (DateTime.now(tz) + daysToSkipt.days).hour(constants.flipHour).minute(0).second(0) toDate ()
  }

  /**
   * Cooldown for reseting purchases. Purchases should be reset in nearest 5am at user's time.
   */
  def getResetPurchasesTimeout = {
    val tz = DateTimeZone.forOffsetHours(user.profile.bio.timezone)
    (DateTime.now(tz) + 1.day).hour(constants.flipHour).minute(0).second(0) toDate ()
  }

  /**
   * Reward for lost quest.
   */
  def rewardForLosingQuest(quest: Quest) = {
    Assets(rating = ratingToLoseQuest(user.profile.level, quest.info.daysDuration))
  }

  /**
   * Reward for won quest.
   */
  def rewardForWinningQuest(quest: Quest) = {
    Assets(rating = ratingToWinQuest(user.profile.level, quest.info.daysDuration))
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
   * ********************
   * Vote quest proposal
   * ********************
   */

  /**
   *
   */
  def canGetQuestProposalForVote = {
    if (user.profile.rights.voteQuestProposals > user.profile.level)
      LevelTooLow
    else if (user.profile.questProposalVoteContext.reviewingQuest != None)
      InvalidState
    else
      OK
  }

  /**
   *
   */
  def canVoteQuestProposal = {
    if (user.profile.rights.voteQuestProposals > user.profile.level)
      LevelTooLow
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

    if (quests.length == 0) {
      None
    } else {
      val rand = new Random(System.currentTimeMillis())
      val random_index = rand.nextInt(quests.length)
      Some(quests(random_index))
    }

  }

  /**
   * Reward for voting for quest proposal.
   */
  def getQuestProposalVoteReward = {
    val level = user.profile.level
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
    if (user.profile.rights.voteQuestSolutions > user.profile.level)
      LevelTooLow
    else if (user.profile.questSolutionVoteContext.reviewingQuestSolution != None)
      InvalidState
    else
      OK
  }

  /**
   * @return None if no more quests to vote for today.
   */
  def getQuestSolutionToVote: Option[QuestSolution] = {
    val quests = api.allQuestSolutionsOnVoting(
      AllQuestSolutionsRequest(user.profile.level - constants.solutionLevelDownTolerance, user.profile.level - constants.solutionLevelUpTolerance)).body.get.quests

    if (quests.length == 0) {
      None
    } else {
      val rand = new Random(System.currentTimeMillis())
      val random_index = rand.nextInt(quests.length)
      Some(quests(random_index))
    }

  }

  /**
   *
   */
  def canVoteQuestSolution = {
    if (user.profile.rights.voteQuestSolutions > user.profile.level)
      LevelTooLow
    else if (user.profile.questSolutionVoteContext.reviewingQuestSolution == None)
      InvalidState
    else
      OK
  }

  /**
   * Reward for quest solution.
   */
  def getQuestSolutionVoteReward = {
    val level = user.profile.level
    val count = user.profile.questSolutionVoteContext.numberOfReviewedSolutions

    if (count < rewardedSolutionVotesPerLevel(level))
      Assets(coins = rewardForVotingSolution(level, count + 1)).clampBot
    else
      Assets()
  }

  /*************************
   * Daily results
   *************************/
  def getStartOfCurrentDailyResultPeriod: Date = {
        val tz = DateTimeZone.forOffsetHours(user.profile.bio.timezone)
    DateTime.now(tz).hour(constants.flipHour).minute(0).second(0) toDate ()
  }
}

