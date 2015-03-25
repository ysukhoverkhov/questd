package logic.user

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
    else if (!bioComplete)
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

}
