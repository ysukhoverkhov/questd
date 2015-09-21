package logic.user

import controllers.domain.app.protocol.ProfileModificationResult._
import logic._
import models.domain.common.Assets
import models.domain.quest.{QuestStatus, Quest}
import models.domain.solution.{SolutionStatus, Solution}
import models.domain.user.profile.Functionality
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

/**
 * All battle challenges related logic.
 */
trait Challenges { this: UserLogic =>

  private def hasChallengeForSolutions(mySolution: Solution, opponentSolution: Solution): Boolean = {
    api.db.challenge.findBySolutions(mySolution.id, opponentSolution.id).nonEmpty
  }

  private def hasChallengeForQuest(myId: String, opponentId: String, questId: String): Boolean = {
    api.db.challenge.findByParticipantsAndQuest((myId, opponentId), questId).nonEmpty
  }

  def canAutoCreatedBattle(
    mySolution: Solution,
    opponentSolution: Solution,
    opponentShouldNotHaveBattles: Boolean,
    checkQuest: Boolean) = {

    lazy val battleCreationDelay = api.config(api.DefaultConfigParams.BattleCreationDelay).toInt

    if (mySolution.info.authorId == opponentSolution.info.authorId)
      OutOfContent
    else if (opponentSolution.battleIds.nonEmpty && opponentShouldNotHaveBattles)
      InvalidState
    else if (hasChallengeForQuest(mySolution.info.authorId, opponentSolution.info.authorId, mySolution.info.questId))
      InvalidState
    else if (checkQuest && (opponentSolution.info.questId != mySolution.info.questId))
      InvalidState
    else if (DateTime.now < (new DateTime(mySolution.info.creationDate) + battleCreationDelay.hour) ||
      DateTime.now < (new DateTime(opponentSolution.info.creationDate) + battleCreationDelay.hour))
      CoolDown
    else
      OK
  }

  // TODO: test me.
  def canChallengeBattleWithSolution(opponentId: String, mySolution: Solution) = {
    lazy val mySolutionExists = user.stats.solvedQuests.values.exists(_ == mySolution.id)

    if (hasChallengeForQuest(user.id, opponentId: String, mySolution.info.questId))
      InvalidState
    else if (mySolution.status != SolutionStatus.InRotation)
      InvalidState
    else if (!mySolutionExists)
      OutOfContent
    else if (!user.profile.rights.unlockedFunctionality.contains(Functionality.ChallengeBattles))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToChallengeBattle))
      NotEnoughAssets
    else
      OK
  }

  // TODO: test me.
  def canChallengeBattleWithQuest(opponentId: String, myQuest: Quest) = {
    lazy val myQuestExists = user.stats.createdQuests.contains(myQuest.id)

    if (hasChallengeForQuest(user.id, opponentId: String, myQuest.id))
      InvalidState
    else if (myQuest.status != QuestStatus.InRotation)
      InvalidState
    else if (!myQuestExists)
      OutOfContent
    else if (!user.profile.rights.unlockedFunctionality.contains(Functionality.ChallengeBattles))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToChallengeBattle))
      NotEnoughAssets
    else
      OK
  }


  // TODO: check for can respond
  /*
    else if (opponentSolution.info.authorId == user.id)
      OutOfContent
    else if (opponentSolution.info.questId != mySolution.info.questId)
      InvalidState
   */

  // TODO: check cost here.
  def costToChallengeBattle = {
    Assets()
  }
}
