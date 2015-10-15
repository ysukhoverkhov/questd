package logic.user

import java.util.Date

import controllers.domain.app.challenge.{AcceptChallengeCode, MakeQuestChallengeCode, MakeSolutionChallengeCode}
import controllers.domain.app.protocol.ProfileModificationResult
import logic.BaseLogicSpecs
import models.domain.challenge.ChallengeStatus
import models.domain.quest.QuestStatus
import models.domain.solution.SolutionStatus
import models.domain.user.friends.{Friendship, FriendshipStatus}
import testhelpers.domainstubs._

class ChallengesSpecs extends BaseLogicSpecs {

  "User Logic for challenges" should {

    "Do not allow auto battles with itself" in context {
      val user = createUserStub()
      val mySolution = createSolutionStub(authorId = user.id)
      val opponentSolution = createSolutionStub(authorId = user.id)

      val rv = user.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = false,
        checkQuest = true)

      rv must beEqualTo(ProfileModificationResult.OutOfContent)
    }

    "Do not allow auto battles with opponent with battles" in context {
      val me = createUserStub()
      val opponent = createUserStub()
      val mySolution = createSolutionStub(authorId = me.id)
      val opponentSolution = createSolutionStub(authorId = opponent.id, battleIds = List("1"))

      val rv = me.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = true,
        checkQuest = true)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do allow auto battles with opponent with battles if allowed" in context {
      val me = createUserStub()
      val opponent = createUserStub()
      val mySolution = createSolutionStub(authorId = me.id)
      val opponentSolution = createSolutionStub(authorId = opponent.id, battleIds = List("1"))

      challenge.findBySolutions(any) returns Iterator.empty
      challenge.findByParticipantsAndQuest(any, any) returns Iterator.empty

      val rv = me.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = false,
        checkQuest = true)

      rv must beEqualTo(ProfileModificationResult.OK)
    }

    "Do not allow battles for solutions from different quests" in context {
      val me = createUserStub()
      val opponent = createUserStub()
      val mySolution = createSolutionStub(authorId = me.id, questId = "qid1")
      val opponentSolution = createSolutionStub(authorId = opponent.id, questId = "qid2")

      challenge.findBySolutions(any) returns Iterator(createChallengeStub())
      challenge.findByParticipantsAndQuest(any, any) returns Iterator(createChallengeStub())

      val rv = me.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = true,
        checkQuest = true)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do allow battles for solutions from different quests if allowed" in context {
      val me = createUserStub()
      val opponent = createUserStub()
      val mySolution = createSolutionStub(authorId = me.id, questId = "qid1")
      val opponentSolution = createSolutionStub(authorId = opponent.id, questId = "qid2")

      challenge.findBySolutions(any) returns Iterator.empty
      challenge.findByParticipantsAndQuest(any, any) returns Iterator.empty

      val rv = me.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = true,
        checkQuest = false)

      rv must beEqualTo(ProfileModificationResult.OK)
    }

    "Do not allow battles for solutions with active challenge" in context {
      val sol1Id = "s1id"
      val sol2Id = "s2id"

      val opponent = createUserStub()
      val me = createUserStub()
      val mySolution = createSolutionStub(id = sol1Id, authorId = me.id)
      val opponentSolution = createSolutionStub(id = sol2Id, authorId = opponent.id)

      challenge.findBySolutions(any) returns Iterator(createChallengeStub())
      challenge.findByParticipantsAndQuest(any, any) returns Iterator(createChallengeStub())

      val rv = me.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = true,
        checkQuest = true)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do not allow battles for solutions created too early" in context {
      val sol1Id = "s1id"
      val sol2Id = "s2id"

      val opponent = createUserStub()
      val me = createUserStub()
      val mySolution = createSolutionStub(id = sol1Id, authorId = me.id, creationDate = new Date())
      val opponentSolution = createSolutionStub(id = sol2Id, authorId = opponent.id)

      challenge.findBySolutions(any) returns Iterator.empty
      challenge.findByParticipantsAndQuest(any, any) returns Iterator.empty

      val rv = me.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = true,
        checkQuest = true)

      rv must beEqualTo(ProfileModificationResult.CoolDown)
    }

    "Do not allow challenging battles for solutions not in rotation" in context {
      val opponentSolution = createSolutionStub()
      val me = createUserStub()
      val opponent = createUserStub(id = opponentSolution.info.authorId)
      val mySolution = createSolutionStub(status = SolutionStatus.CheatingBanned)

      challenge.findBySolutions(any) returns Iterator.empty
      challenge.findByParticipantsAndQuest(any, any) returns Iterator.empty

      val rv = me.canChallengeWithSolution(opponent, mySolution)

      rv must beEqualTo(MakeSolutionChallengeCode.SolutionNotInRotation)
    }

    "Do not allow challenging for quests not in rotation" in context {
      val opponentSolution = createSolutionStub()
      val me = createUserStub(friends = List(Friendship(friendId = opponentSolution.info.authorId, status = FriendshipStatus.Accepted)))
      val myQuest = createQuestStub(status = QuestStatus.CheatingBanned)

      challenge.findBySolutions(any) returns Iterator.empty
      challenge.findByParticipantsAndQuest(any, any) returns Iterator.empty

      val rv = me.canChallengeWithQuest(opponentSolution.info.authorId, myQuest)

      rv must beEqualTo(MakeQuestChallengeCode.QuestNotInRotation)
    }

    "Do not allow challenging for quests if opponent not our friend" in context {
      val myQuest = createQuestStub(status = QuestStatus.InRotation)
      val me = createUserStub(createdQuests = List(myQuest.id))
      val opponentSolution = createSolutionStub()

      challenge.findBySolutions(any) returns Iterator.empty
      challenge.findByParticipantsAndQuest(any, any) returns Iterator.empty

      val rv = me.canChallengeWithQuest(opponentSolution.info.authorId, myQuest)

      rv must beEqualTo(MakeQuestChallengeCode.OpponentNotAFriend)
    }


    "Accept correct challenges" in context {
      val questId = "questId"
      val me = createUserStub()
      val challenge = createChallengeStub(questId = questId, opponentId = me.id, status = ChallengeStatus.Requested)
      val solution = createSolutionStub(questId = questId)

      val rv = me.canAcceptChallengeWithSolution(challenge, solution)

      rv must beEqualTo(AcceptChallengeCode.OK)
    }
  }
}

