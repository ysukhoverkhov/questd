package logic.user

import controllers.domain.app.user.SolveQuestCode
import logic._
import models.domain.common.ContentType._
import models.domain.quest.Quest
import models.domain.solution.SolutionInfoContent
import models.domain.user.profile.Functionality

/**
 * All logic related to solving quests.
 */
trait SolvingQuests { this: UserLogic =>

  /**
   * Checks is user potentially able to solve quests today (disregarding coins and other things).
   */
  def canSolveQuestToday = {
    user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoSolutions)
  }

  /**
   * Is user can propose quest of given type.
   */
  def canSolveQuest(questToSolve: Quest, solutionContent: SolutionInfoContent): SolveQuestCode.Value = {
    import SolveQuestCode._

    val content = solutionContent.media.map(_.contentType).getOrElse(Photo) match {
      case Photo => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoSolutions)
      case Video => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitVideoSolutions)
    }

    if (!content)
      NotEnoughRights
    else if (!(user.profile.assets canAfford questToSolve.info.solveCost))
      NotEnoughAssets
    else if (user.stats.solvedQuests.contains(questToSolve.id))
      QuestAlreadySolved
    else if (solutionContent.description.getOrElse("").length > api.config(api.DefaultConfigParams.SolutionMaxDescriptionLength).toInt)
      DescriptionLengthLimitExceeded
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
