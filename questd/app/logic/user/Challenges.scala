package logic.user

import controllers.domain.app.protocol.ProfileModificationResult._
import logic._
import models.domain.challenge.{ChallengeStatus, Challenge}
import models.domain.common.Assets
import models.domain.quest.{QuestStatus, Quest}
import models.domain.solution.{SolutionStatus, Solution}
import models.domain.user.User
import models.domain.user.friends.FriendshipStatus
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

  def canChallengeWithSolution(opponent: User, mySolution: Solution) = {
    lazy val mySolutionExists = user.stats.solvedQuests.values.exists(_ == mySolution.id)
    lazy val isMyFriend = user.friends.exists(f => f.friendId == opponent.id && f.status == FriendshipStatus.Accepted)
    lazy val solvedSameQuest = opponent.stats.solvedQuests.contains(mySolution.info.questId)

    if (hasChallengeForQuest(user.id, opponent.id: String, mySolution.info.questId))
      InvalidState
    else if (mySolution.status != SolutionStatus.InRotation)
      InvalidState
    else if (!(isMyFriend || solvedSameQuest)) // TODO: test it.
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

  def canChallengeWithQuest(opponentId: String, myQuest: Quest) = {
    lazy val myQuestExists = user.stats.createdQuests.contains(myQuest.id)
    lazy val isMyFriend = user.friends.exists(f => f.friendId == opponentId && f.status == FriendshipStatus.Accepted)

    if (hasChallengeForQuest(user.id, opponentId: String, myQuest.id))
      InvalidState
    else if (myQuest.status != QuestStatus.InRotation)
      InvalidState
    else if (!myQuestExists)
      OutOfContent
    else if (!isMyFriend)
      InvalidState
    else if (!user.profile.rights.unlockedFunctionality.contains(Functionality.ChallengeBattles))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToChallengeBattle))
      NotEnoughAssets
    else
      OK
  }

  def canAcceptChallengeWithSolution(challenge: Challenge, solution: Solution) = {
    if (challenge.questId != solution.info.questId)
      InvalidState
    else if (challenge.opponentId != user.id)
      InvalidState
    else if (challenge.status != ChallengeStatus.Requested)
      InvalidState
    else
      OK
  }

  def canRejectChallenge(challenge: Challenge) = {
    if (challenge.opponentId != user.id)
      InvalidState
    else if (challenge.status != ChallengeStatus.Requested)
      InvalidState
    else
      OK
  }

  // check cost here.
  def costToChallengeBattle = {
    Assets()
  }
}
