package controllers.domain.app.user

import controllers.domain._
import controllers.domain.app.challenge._
import controllers.domain.app.protocol.ProfileModificationResult
import models.domain.challenge.ChallengeStatus
import models.domain.user.friends.{Friendship, FriendshipStatus}
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class ChallengesAPISpecs extends BaseAPISpecs {

  "Challenges API" should {

    "makeQuestChallenge works" in context {
      val myQuestId = "mysolid"
      val q = createQuestStub(id = myQuestId)
      val opponent = createUserStub()
      val u1 = createUserStub(
        createdQuests = List(myQuestId),
        friends = List(Friendship(friendId = opponent.id, status = FriendshipStatus.Accepted)))

      quest.readById(myQuestId) returns Some(q)
      challenge.findByParticipantsAndQuest((u1.id, opponent.id), myQuestId) returns Iterator.empty

      val result = api.makeQuestChallenge(MakeQuestChallengeRequest(
        user = u1,
        opponentId = opponent.id,
        myQuestId = myQuestId))

      result must beAnInstanceOf[OkApiResult[MakeQuestChallengeResult]]

      there was one(challenge).create(any)
      there was one(api).makeTask(any)
    }

    "makeSolutionChallenge works" in context {
      val questId = "questId"
      val mySolutionId = "mySolutionId"
      val sol = createSolutionStub(id = mySolutionId, questId = questId)
      val opponent = createUserStub()
      val u1 = createUserStub(solvedQuests = Map(questId -> mySolutionId), friends = List(Friendship(friendId = opponent.id, status = FriendshipStatus.Accepted)))

      solution.readById(mySolutionId) returns Some(sol)
      user.readById(opponent.id) returns Some(opponent)
      challenge.findByParticipantsAndQuest((u1.id, opponent.id), questId) returns Iterator.empty

      val result = api.makeSolutionChallenge(MakeSolutionChallengeRequest(
        user = u1,
        opponentId = opponent.id,
        mySolutionId = mySolutionId))

      result must beAnInstanceOf[OkApiResult[MakeSolutionChallengeResult]]

      there was one(challenge).create(any)
      there was one(api).makeTask(any)
    }

    "Do not accept not existing challenges" in context {
      val me = createUserStub()
      val challengeId = "challengeId"
      val solutionId = "solutionId"

      db.challenge.readById(challengeId) returns None

      val result = api.acceptChallenge(AcceptChallengeRequest(me, challengeId, solutionId))

      result must beEqualTo(OkApiResult(AcceptChallengeResult(AcceptChallengeCode.ChallengeNotFound)))
    }

    "Do not accept not existing solutions" in context {
      val me = createUserStub()
      val challengeId = "challengeId"
      val challenge = createChallengeStub(id = challengeId)
      val mySolutionId = "mySolutionId"

      db.challenge.readById(challengeId) returns Some(challenge)
      db.solution.readById(mySolutionId) returns None

      val result = api.acceptChallenge(AcceptChallengeRequest(me, challengeId, mySolutionId))

      result must beEqualTo(OkApiResult(AcceptChallengeResult(AcceptChallengeCode.SolutionNotFound)))
    }

    "Do not accept challenge if logic forbids" in context {
      val me = createUserStub()
      val challengeId = "challengeId"
      val challenge = createChallengeStub(id = challengeId)
      val mySolutionId = "mySolutionId"
      val mySolution = createSolutionStub(id = mySolutionId)

      db.challenge.readById(challengeId) returns Some(challenge)
      db.solution.readById(mySolutionId) returns Some(mySolution)

      val result = api.acceptChallenge(AcceptChallengeRequest(me, challengeId, mySolutionId))

      result.body.get.allowed must beEqualTo(ProfileModificationResult.OK).not
    }

    "Do accept challenge if everything is ok" in context {
      val me = createUserStub()
      val opponent = createUserStub()
      val questId = "questId"
      val challengeId = "challengeId"
      val mySolutionId = "mySolutionId"
      val mySolution = createSolutionStub(
        id = mySolutionId,
        questId = questId)
      val opponentSolutionId = "opponentSolutionId"
      val opponentSolution = createSolutionStub(
        id = opponentSolutionId,
        questId = questId)
      val challenge = createChallengeStub(
        id = challengeId,
        status = ChallengeStatus.Requested,
        opponentId = me.id,
        questId = questId,
        myId = opponent.id,
        mySolutionId = Some(opponentSolutionId))

      db.challenge.readById(challengeId) returns Some(challenge)
      db.solution.readById(mySolutionId) returns Some(mySolution)
      db.challenge.updateChallenge(challenge.id, ChallengeStatus.Accepted, Some(mySolution.id)) returns Some(challenge)
      db.user.readById(opponent.id) returns Some(opponent)
      doReturn(OkApiResult(SendMessageResult(opponent))).when(api).sendMessage(any)
      db.solution.readById(opponentSolutionId) returns Some(opponentSolution)
      doReturn(OkApiResult(CreateBattleResult())).when(api).createBattle(any)
      db.user.readById(me.id) returns Some(me)

      val result = api.acceptChallenge(AcceptChallengeRequest(me, challengeId, mySolutionId))

      result must beAnInstanceOf[OkApiResult[AcceptChallengeResult]]
      result.body.get.allowed must beEqualTo(AcceptChallengeCode.OK)

      there was one(db.challenge).updateChallenge(challenge.id, ChallengeStatus.Accepted, Some(mySolution.id))
      there was one(api).sendMessage(any)
      there was one(api).createBattle(any)
    }

    "Auto rejecting of a challenge is working" in context {
      val u = createUserStub()
      val c = createChallengeStub(opponentId = u.id)

      db.user.readById(u.id) returns Some(u)
      doReturn(OkApiResult(RejectChallengeResult(RejectChallengeCode.OK))).when(api).rejectChallenge(any)

      val result = api.autoRejectChallenge(AutoRejectChallengeRequest(c))

      result must beEqualTo(OkApiResult(AutoRejectChallengeResult()))

      there was one(api).rejectChallenge(any)
    }

//    "acceptChallenge creates battle" in context {
//      val mySolutionId = "mysolid"
//      val opponentSolutionId = "opsolid"
//      val q = createQuestStub()
//      val sol1 = createSolutionStub(id = mySolutionId, questId = q.id)
//      val sol2 = createSolutionStub(id = opponentSolutionId, questId = q.id)
//      val opponent = createUserStub()
//      val u1 = createUserStub(solvedQuests = Map(q.id -> sol1.id))
//
////      , battleRequests = List(
////        Challenge(
////          opponentId = opponent.id,
////          mySolutionId = sol1.id,
////          opponentSolutionId = sol2.id,
////          status = ChallengeStatus.Requests
////        ))
//
////      user.updateBattleRequest(any, any, any, any) returns Some(u1)
//      solution.readById(sol1.id) returns Some(sol1)
//      solution.readById(sol2.id) returns Some(sol2)
//      user.readById(any) returns Some(u1)
//      user.recordBattleParticipation(any, any, any) returns Some(u1)
//      user.addEntryToTimeLine(any, any) returns Some(u1)
//      user.addMessage(any, any) returns Some(u1)
//
//      val result = api.respondChallenge(RespondChallengeRequest(
//        user = u1,
//        opponentSolutionId = opponentSolutionId,
//        accept = true))
//
//      there were two(user).updateBattleRequest(any, any, any, any)
//      there was one(battle).create(any)
//
//      result must beAnInstanceOf[OkApiResult[RespondChallengeResult]]
//    }
  }
}

