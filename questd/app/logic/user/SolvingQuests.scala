package logic.user

import java.util.Date
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._
import play.Logger
import logic._
import logic.constants._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import models.domain.base._
import models.domain.ContentType._
import controllers.domain.admin._
import controllers.domain._
import controllers.domain.app.user._

/**
 * All logic related to solving quests.
 */
trait SolvingQuests { this: UserLogic =>

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
    val quests = getQuests

    selectQuest(quests, user.history.solvedQuestIds) orElse {

      val regularQuests = getOtherQuests.getOrElse(List().iterator)
      selectQuest(quests, user.history.solvedQuestIds)
    } orElse {

      val allQuests = api.allQuestsInRotation(
        AllQuestsRequest(
          user.profile.publicProfile.level - questLevelToleranceDown,
          user.profile.publicProfile.level + questLevelToleranceUp)).body.get.quests
      selectQuest(allQuests, user.history.solvedQuestIds)
    }
  }

  private def getQuests = {
    List(
      () => getTutorialQuests,
      () => getStartingQuests,
      () => getDefaultQuests).
      foldLeft[Option[Iterator[Quest]]](None)((run, fun) => {
        if (run == None) fun() else run
      }).
      getOrElse(List().iterator)
  }

  private def getTutorialQuests: Option[Iterator[Quest]] = {
    Logger.trace("getTutorialQuests")
    None
  }

  private def getStartingQuests: Option[Iterator[Quest]] = {
    Logger.trace("getStartingQuests") // till level 5 including.

    // TODO: move level 5 to config.
    // TODO perhaps move this level further if tutorial will end not so soon.
    if (user.profile.publicProfile.level > 5) {
      None
    } else {
      // TODO: decide should we make it for sure or with some probability.
      // TODO: move 0.5 to config.
      if (rand.nextDouble < 0.5) {
        getVIPQuests
      } else {
        getOtherQuests
      }
    }

  }

  private def getDefaultQuests: Option[Iterator[Quest]] = {
    Logger.trace("getDefaultQuests")

    val dice = rand.nextDouble

    List(
      (api.config(api.ConfigParams.QuestProbabilityFriends).toDouble, () => getFriendsQuests),
      (api.config(api.ConfigParams.QuestProbabilityShortlist).toDouble, () => getShortlistQuests),
      (api.config(api.ConfigParams.QuestProbabilityLiked).toDouble, () => getLikedQuests),
      (api.config(api.ConfigParams.QuestProbabilityStar).toDouble, () => getVIPQuests),
      (1.00, () => getOtherQuests) // 1.00 - Last one in the list is 1 to ensure quest will be selected.
      ).foldLeft[Either[Double, Option[Iterator[Quest]]]](Left(0))((run, fun) => {
        run match {
          case Left(p) => {
            val curProbabiliy = p + fun._1
            if (curProbabiliy > dice) {
              Right(fun._2())
            } else {
              Left(curProbabiliy)
            }
          }
          case _ => run
        }
      }) match {
        case Right(oi) => oi match {
          case Some(i) => if (i.hasNext) Some(i) else None
          case None => None
        }
        case Left(_) => {
          Logger.error("getDefaultQuests - None of quest selector functions were called. Check probabilities.")
          None
        }
      }
  }

  private def getFriendsQuests = {
    Logger.trace("Returning quest from friends")
    Some(api.getFriendsQuests(GetFriendsQuestsRequest(
      user,
      user.profile.publicProfile.level - questLevelToleranceDown,
      user.profile.publicProfile.level + questLevelToleranceUp)).body.get.quests)
  }

  private def getShortlistQuests = {
    Logger.trace("Returning quest from shortlist")
    Some(api.getShortlistQuests(GetShortlistQuestsRequest(
      user,
      user.profile.publicProfile.level - questLevelToleranceDown,
      user.profile.publicProfile.level + questLevelToleranceUp)).body.get.quests)
  }

  private def getLikedQuests = {
    Logger.trace("Returning quests we liked recently")
    Some(api.getLikedQuests(GetLikedQuestsRequest(
      user,
      user.profile.publicProfile.level - questLevelToleranceDown,
      user.profile.publicProfile.level + questLevelToleranceUp)).body.get.quests)
  }

  private def getVIPQuests = {
    Logger.trace("Returning VIP quests")

    val themeIds = selectRandomThemes(numberOfFavoriteThemesForVIPQuests)
    Logger.trace("Selected themes of vip's quests: " + themeIds.mkString(", "))

    Some(api.getVIPQuests(GetVIPQuestsRequest(
      user,
      user.profile.publicProfile.level - questLevelToleranceDown,
      user.profile.publicProfile.level + questLevelToleranceUp,
      themeIds)).body.get.quests)
  }

  private def getOtherQuests = {
    Logger.trace("getOtherQuests")

    // TODO: record themes of all selected quests.
    val themeIds = selectRandomThemes(numberOfFavoriteThemesForOtherQuests)
    Logger.trace("Selected themes of other quests: " + themeIds.mkString(", "))

    Some(api.getAllQuests(GetAllQuestsRequest(
      user,
      user.profile.publicProfile.level - questLevelToleranceDown,
      user.profile.publicProfile.level + questLevelToleranceUp,
      themeIds)).body.get.quests)
  }

  private def selectRandomThemes(count: Int): List[String] = {
    if (user.history.themesOfSelectedQuests.length > 0) {
      for (i <- (1 to count).toList) yield {
        user.history.themesOfSelectedQuests(rand.nextInt(user.history.themesOfSelectedQuests.length))
      }
    } else {
      List()
    }
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

}