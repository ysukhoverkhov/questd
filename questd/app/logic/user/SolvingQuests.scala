package logic.user

import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._
import logic._
import logic.constants._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import models.domain.ContentType._

/**
 * All logic related to solving quests.
 */
trait SolvingQuests { this: UserLogic =>

  /**
   * Check can the user purchase quest.
   */
//  def canPurchaseQuest = {
//    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoResults))
//      NotEnoughRights
//    else if (!(user.profile.assets canAfford costOfPurchasingQuest))
//      NotEnoughAssets
//    else if (user.profile.questSolutionContext.questCooldown.after(new Date()))
//      CoolDown
//    else if (user.profile.questSolutionContext.takenQuest != None)
//      InvalidState
//    else if (user.demo.cultureId == None || user.profile.publicProfile.bio.gender == Gender.Unknown)
//      IncompleteBio
//    else
//      OK
//  }

  /**
   * Checks is user potentially able to solve quests today (disregarding coins and other things).
   */
//  def canSolveQuestToday = {
//    user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoResults) &&
//    user.profile.questSolutionContext.questCooldown.before(new Date())
//  }

  /**
   * Tells cost of next quest purchase
   */
//  def costOfPurchasingQuest = {
//    if (user.profile.questSolutionContext.numberOfPurchasedQuests <= NumberOfQuestsSkipsForCoins) {
//
//      val questDuration = user.profile.questSolutionContext.purchasedQuest match {
//        case Some(QuestInfoWithID(_, q)) => q.daysDuration
//        case _ => 1
//      }
//
//      val c = costToSkipQuest(user.profile.publicProfile.level, user.profile.questSolutionContext.numberOfPurchasedQuests, questDuration)
//      Assets(coins = c)
//    } else {
//      Assets(money = 1)
//    }
//  }

  /**
   * Takes everything into account and returns possible quest to be solved by user.
   */
  // TODO: clean me up.
//  def getRandomQuestForSolution: Option[Quest] = {
//    getRandomQuest(QuestGetReason.ForSolving)
//  }

  /**
   * Check are we able to take quest.
   */
//  def canTakeQuest = {
//    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoResults))
//      NotEnoughRights
//    else if (user.profile.questSolutionContext.purchasedQuest == None)
//      InvalidState
//    else if (!(user.profile.assets canAfford costOfTakingQuest))
//      NotEnoughAssets
//    else
//      OK
//  }

  /**
   * Get cost of taking quest to resolve.
   */
  // TODO: rename to costOfSolvingQuest
  def costOfTakingQuest = {
    Assets(coins = costToTakeQuestToSolve(user.profile.publicProfile.level/*, purchasedQuestDuration*/))
  }

  /**
   * Is user can propose quest of given type.
   */// TODO: rename me to "canSolveQuest"
  def canResolveQuest(contentType: ContentType, questId: String /*, friendsInvited: Int*/) = {
    val content = contentType match {
      case Photo => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoResults)
      case Video => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitVideoResults)
    }

    // TODO check cost of solving quest here.
    // TODO: check we have this quest in time line.
    if (!content)
      NotEnoughRights
//    else if (user.profile.questSolutionContext.takenQuest == None)
//      InvalidState
//    else if (!(user.profile.assets canAfford costOfAskingForHelpWithSolution * friendsInvited))
//      NotEnoughAssets
    else
      OK
  }

  /**
   * Is user can give up quest.
   */
//  def canGiveUpQuest = {
//    if (user.profile.questSolutionContext.takenQuest == None)
//      InvalidState
//    else
//      OK
//  }

  /**
   * How much it'll cost to give up taken quest.
   */
//  def costOfGivingUpQuest = {
//    Assets(rating = ratingToGiveUpQuest(user.profile.publicProfile.level, takenQuestDuration)) clampTop user.profile.assets
//  }

  /**
   * How much it'll be for a single friend to help us with proposal.
   */
  def costOfAskingForHelpWithSolution = {
    Assets(coins = coinsToInviteFriendForVoteQuestSolution(user.profile.publicProfile.level))
  }

  // TODO: clean me up.
//  /**
//   * Cooldown for taking quest.
//   */
//  def getCooldownForTakeQuest(qi: QuestInfo) = {
//    val daysToSkipt = qi.daysDuration
//
//    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
//    (DateTime.now(tz) + daysToSkipt.days).hour(constants.FlipHour).minute(0).second(0) toDate ()
//  }

//  /**
//   * Time to solve quest.
//   */
//  def getDeadlineForTakeQuest(qi: QuestInfo) = {
//    val minutesToSolveQuest = qi.minutesDuration
//
//    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
//    (DateTime.now(tz) + minutesToSolveQuest.minutes) toDate ()
//  }

  /**
   * Cooldown for reseting purchases. Purchases should be reset in nearest 5am at user's time.
   */
  def getResetPurchasesTimeout = getNextFlipHourDate

  /**
   * Time when to stop voring for solution.
   */
  def solutionVoteEndDate(qi: QuestInfo) = {
    val mult = qi.level match {
      case x if 1 to 10 contains x => 1
      case x if 11 to 16 contains x => 2
      case _ => 3
    }

    DateTime.now + mult.days toDate ()
  }

  /**
   * Reward for lost quest.
   */ // TODO: implement me.
  def rewardForLosingQuest(quest: Quest) = {
//    Assets(rating = ratingToLoseQuest(user.profile.publicProfile.level, quest.info.daysDuration)) * api.config(api.ConfigParams.DebugExpMultiplier).toDouble
    Assets()
  }

  /**
   * Reward for won quest.
   */ // TODO: implement me.
  def rewardForWinningQuest(quest: Quest) = {
//    Assets(rating = ratingToWinQuest(user.profile.publicProfile.level, quest.info.daysDuration)) * api.config(api.ConfigParams.DebugExpMultiplier).toDouble
    Assets()
  }

  /**
   * Penalty for cheating solution
   */
  def penaltyForCheatingSolution(quest: Quest) = {
    (rewardForLosingQuest(quest) * QuestSolutionCheatingPenalty) clampTop user.profile.assets
  }

  /**
   * Penalty for IAC solution
   */
  def penaltyForIACSolution(quest: Quest) = {
    (rewardForLosingQuest(quest) * QuestSolutionIACPenalty) clampTop user.profile.assets
  }

  /**
   * Returns taken quest duration in days.
   */
//  private def takenQuestDuration = {
//    questDuration(user.profile.questSolutionContext.takenQuest)
//  }

  /**
   * Returns purchased quest duration in days.
   */
//  private def purchasedQuestDuration = {
//    questDuration(user.profile.questSolutionContext.purchasedQuest)
//  }

//  private def questDuration(q: Option[QuestInfoWithID]) = {
//    q match {
//      case Some(QuestInfoWithID(_, i)) => i.daysDuration
//      case None => 0
//    }
//  }

  /**
   * Check is quest deadline passed and quest should be autogave up.
   */
//  def questDeadlineReached = {
//    ((user.profile.questSolutionContext.takenQuest != None)
//      && user.profile.questSolutionContext.questDeadline.before(new Date()))
//  }

}
