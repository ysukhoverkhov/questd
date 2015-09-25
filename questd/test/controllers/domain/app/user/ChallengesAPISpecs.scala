package controllers.domain.app.user

import controllers.domain._
import controllers.domain.app.challenge.{MakeQuestChallengeRequest, MakeQuestChallengeResult, MakeSolutionChallengeRequest, MakeSolutionChallengeResult}
import models.domain.user.friends.{Friendship, FriendshipStatus}
import play.Logger
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

      Logger.error(s"$result")

      result must beAnInstanceOf[OkApiResult[MakeSolutionChallengeResult]]

      there was one(challenge).create(any)
      there was one(api).makeTask(any)
    }

    "acceptChallenge creates battle" in context {
      val mySolutionId = "mysolid"
      val opponentSolutionId = "opsolid"
      val q = createQuestStub()
      val sol1 = createSolutionStub(id = mySolutionId, questId = q.id)
      val sol2 = createSolutionStub(id = opponentSolutionId, questId = q.id)
      val opponent = createUserStub()
      val u1 = createUserStub(solvedQuests = Map(q.id -> sol1.id))

//      , battleRequests = List(
//        Challenge(
//          opponentId = opponent.id,
//          mySolutionId = sol1.id,
//          opponentSolutionId = sol2.id,
//          status = ChallengeStatus.Requests
//        ))

//      user.updateBattleRequest(any, any, any, any) returns Some(u1)
      solution.readById(sol1.id) returns Some(sol1)
      solution.readById(sol2.id) returns Some(sol2)
      user.readById(any) returns Some(u1)
      user.recordBattleParticipation(any, any, any) returns Some(u1)
      user.addEntryToTimeLine(any, any) returns Some(u1)
      user.addMessage(any, any) returns Some(u1)

      val result = api.respondChallenge(RespondChallengeRequest(
        user = u1,
        opponentSolutionId = opponentSolutionId,
        accept = true))

      there were two(user).updateBattleRequest(any, any, any, any)
      there was one(battle).create(any)

      result must beAnInstanceOf[OkApiResult[RespondChallengeResult]]
    }
  }
}

