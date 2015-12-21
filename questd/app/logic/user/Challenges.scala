package logic.user

import com.github.nscala_time.time.Imports._
import controllers.domain.app.challenge.{RejectChallengeCode, AcceptChallengeCode, MakeSolutionChallengeCode, MakeQuestChallengeCode}
import logic._
import models.domain.challenge.{ChallengeStatus, Challenge}
import models.domain.common.Assets
import models.domain.quest.{QuestStatus, Quest}
import models.domain.solution.{Solution, SolutionStatus}
import models.domain.user.User
import models.domain.user.friends.FriendshipStatus
import models.domain.user.profile.Functionality
import org.joda.time.DateTime

/**
 * All challenges related logic.
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
    checkQuest: Boolean): Boolean = {

    lazy val battleCreationDelay = api.config(api.DefaultConfigParams.BattleCreationDelay).toInt

    if (mySolution.info.authorId == opponentSolution.info.authorId)
      false
    else if (opponentSolution.battleIds.nonEmpty && opponentShouldNotHaveBattles)
      false
    else if (hasChallengeForQuest(mySolution.info.authorId, opponentSolution.info.authorId, mySolution.info.questId))
      false
    else if (checkQuest && (opponentSolution.info.questId != mySolution.info.questId))
      false
    else if (DateTime.now < (new DateTime(mySolution.info.creationDate) + battleCreationDelay.hour) ||
      DateTime.now < (new DateTime(opponentSolution.info.creationDate) + battleCreationDelay.hour))
      false
    else
      true
  }

  def canChallengeWithSolution(opponent: User, mySolution: Solution): MakeSolutionChallengeCode.Value = {
    import MakeSolutionChallengeCode._

    lazy val mySolutionExists = user.stats.solvedQuests.values.exists(_ == mySolution.id)
    lazy val isMyFriend = user.friends.exists(f => f.friendId == opponent.id && f.status == FriendshipStatus.Accepted)
    lazy val solvedSameQuest = opponent.stats.solvedQuests.contains(mySolution.info.questId)

    if (hasChallengeForQuest(user.id, opponent.id: String, mySolution.info.questId))
      OpponentAlreadyChallenged
    else if (mySolution.status != SolutionStatus.InRotation)
      SolutionNotInRotation
    else if (!(isMyFriend || solvedSameQuest))
      OpponentNotAFriendAndDoesNotHaveSolution
    else if (!mySolutionExists)
      SolutionNotFound
    else if (!user.profile.rights.unlockedFunctionality.contains(Functionality.ChallengeBattles))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToChallengeBattle))
      NotEnoughAssets
    else
      OK
  }

  def canChallengeWithQuest(opponentId: String, myQuest: Quest): MakeQuestChallengeCode.Value = {
    import MakeQuestChallengeCode._

    lazy val myQuestExists = user.stats.createdQuests.contains(myQuest.id)
    lazy val isMyFriend = user.friends.exists(f => f.friendId == opponentId && f.status == FriendshipStatus.Accepted)

    if (hasChallengeForQuest(user.id, opponentId: String, myQuest.id))
      OpponentAlreadyChallenged
    else if (myQuest.status != QuestStatus.InRotation)
      QuestNotInRotation
    else if (!myQuestExists)
      QuestNotFound
    else if (!isMyFriend)
      OpponentNotAFriend
    else if (!user.profile.rights.unlockedFunctionality.contains(Functionality.ChallengeBattles))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToChallengeBattle))
      NotEnoughAssets
    else
      OK
  }

  def canAcceptChallengeWithSolution(challenge: Challenge, solution: Solution): AcceptChallengeCode.Value = {
    import AcceptChallengeCode._

    if (challenge.questId != solution.info.questId)
      SolutionNotForTheQuest
    else if (challenge.opponentId != user.id)
      CannotAcceptOwnChallenge
    else if (challenge.status != ChallengeStatus.Requested)
      WrongChallengeState
    else
      OK
  }

  def canRejectChallenge(challenge: Challenge): RejectChallengeCode.Value = {
    import RejectChallengeCode._

    if (challenge.opponentId != user.id)
      CannotAcceptOwnChallenge
    else if (challenge.status != ChallengeStatus.Requested)
      WrongChallengeState
    else
      OK
  }

  // check cost here.
  def costToChallengeBattle = {
    Assets()
  }
}
