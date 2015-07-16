package logic.user

import controllers.domain.app.protocol.ProfileModificationResult._
import logic._
import models.domain.common.Assets
import models.domain.solution.Solution
import models.domain.user.profile.Functionality
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

/**
 * All battle challenges related logic.
 */
trait Challenges { this: UserLogic =>

  def canAutoCreatedBattle(
    mySolution: Solution,
    opponentSolution: Solution,
    opponentShouldNotHaveBattles: Boolean,
    checkQuest: Boolean) = {

    lazy val alreadyHasRequest = user.battleRequests
      .exists(br => (br.mySolutionId == mySolution.id) && (br.opponentSolutionId == opponentSolution.id))

    if (mySolution.info.authorId == opponentSolution.info.authorId)
      OutOfContent
    else if (opponentSolution.battleIds.nonEmpty && opponentShouldNotHaveBattles)
      InvalidState
    else if (alreadyHasRequest)
      InvalidState
    else if (checkQuest && (opponentSolution.info.questId != mySolution.info.questId))
      InvalidState
    else if (DateTime.now < (new DateTime(mySolution.creationDate) + 1.hour) ||
      DateTime.now < (new DateTime(opponentSolution.creationDate) + 1.hour)) // TODO: test the branch.
      // TODO: get the period in config.
      InvalidState
    else
      OK
  }

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
    else if (!user.profile.rights.unlockedFunctionality.contains(Functionality.ChallengeBattles))
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
