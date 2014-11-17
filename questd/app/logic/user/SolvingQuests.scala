package logic.user

import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._
import logic._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import models.domain.ContentType._

/**
 * All logic related to solving quests.
 */
trait SolvingQuests { this: UserLogic =>

  /**
   * Checks is user potentially able to solve quests today (disregarding coins and other things).
   */
  def canSolveQuestToday = {
    user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoResults)
  }

  /**
   * Is user can propose quest of given type.
   */
  def canSolveQuest(contentType: ContentType, questToSolve: Quest) = {
    val content = contentType match {
      case Photo => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoResults)
      case Video => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitVideoResults)
    }

    if (!content)
      NotEnoughRights
    else if (!user.timeLine.map(_.objectId).contains(questToSolve.id))
      OutOfContent
    else if (!(user.profile.assets canAfford questToSolve.info.solveCost))
      NotEnoughAssets
    else if (questToSolve.info.authorId == user.id)
      InvalidState
    else if (user.stats.solvedQuests.contains(questToSolve.id))
      InvalidState
    else if (user.demo.cultureId == None || user.profile.publicProfile.bio.gender == Gender.Unknown)
      IncompleteBio
    else
      OK
  }

  /**
   * How much it'll be for a single friend to help us with proposal.
   */
//  def costOfAskingForHelpWithSolution = {
//    Assets(coins = coinsToInviteFriendForVoteQuestSolution(user.profile.publicProfile.level))
//  }

  /**
   * Cooldown for resetting purchases. Purchases should be reset in nearest 5am at user's time.
   */
  def getResetPurchasesTimeout = getNextFlipHourDate

  /**
   * Time when to stop voting for solution.
   */
  def solutionVoteEndDate(qi: QuestInfo) = {
    val coef = qi.level match {
      case x if 1 to 10 contains x => 1
      case x if 11 to 16 contains x => 2
      case _ => 3
    }

    DateTime.now + coef.days toDate ()
  }

}
