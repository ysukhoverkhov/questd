package logic.user

import controllers.domain.app.protocol.ProfileModificationResult._
import logic._
import models.domain.common.Assets
import models.domain.solution.Solution
import models.domain.user.profile.Functionality

/**
 * All battle challenges related logic.
 */
trait Challenges { this: UserLogic =>

  // TODO: check rights here.
  def canChallengeBattle(mySolution: Solution, opponentSolution: Solution) = {
    lazy val mySolutionExists = user.stats.solvedQuests.values.exists(_ == mySolution.id)
    lazy val alreadyHasRequest = user.battleRequests
      .exists(br => (br.mySolutionId == mySolution.id) && (br.opponentSolutionId == opponentSolution.id))

    if (alreadyHasRequest)
      InvalidState
    else if (!mySolutionExists)
      OutOfContent
    else if (opponentSolution.info.authorId == user.id)
      OutOfContent
    else if (opponentSolution.info.questId != mySolution.info.questId)
      InvalidState
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.ChallengeBattles))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToChallengeBattle))
      NotEnoughAssets
    else
      OK
  }

  // TODO: check cost here.
  def costToChallengeBattle = {
    Assets()
  }
}
