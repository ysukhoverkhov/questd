package controllers.domain.app.user

import controllers.domain._
import models.domain.chat.{Conversation, Participant}
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class ConversationsAPISpecs extends BaseAPISpecs {

  "Conversations API" should {

    "Return all active conversations" in context {
      val u = createUserStub()

      conversation.findByParticipant(u.id) returns List(Conversation(participants = List(Participant("1")))).iterator

      val result = api.getMyConversations(GetMyConversationsRequest(
        user = u))

      there was one(conversation).findByParticipant(u.id)

      result must beAnInstanceOf[OkApiResult[GetMyConversationsResult]]
    }

//    "respondBattleRequest crates battle" in context {
//      val mySolutionId = "mysolid"
//      val opponentSolutionId = "opsolid"
//      val q = createQuestStub()
//      val sol1 = createSolutionStub(id = mySolutionId, questId = q.id)
//      val sol2 = createSolutionStub(id = opponentSolutionId, questId = q.id)
//      val opponent = createUserStub()
//      val u1 = createUserStub(solvedQuests = Map(q.id -> sol1.id), battleRequests = List(
//        BattleRequest(
//          opponentId = opponent.id,
//          mySolutionId = sol1.id,
//          opponentSolutionId = sol2.id,
//          status = BattleRequestStatus.Requests
//        )))
//
//      user.updateBattleRequest(any, any, any, any) returns Some(u1)
//      solution.readById(sol1.id) returns Some(sol1)
//      solution.readById(sol2.id) returns Some(sol2)
//      user.readById(any) returns Some(u1)
//      user.recordBattleParticipation(any, any, any) returns Some(u1)
//      user.addEntryToTimeLine(any, any) returns Some(u1)
//      user.addMessage(any, any) returns Some(u1)
//
//      val result = api.respondBattleRequest(RespondBattleRequestRequest(
//        user = u1,
//        opponentSolutionId = opponentSolutionId,
//        accept = true))
//
//      there were two(user).updateBattleRequest(any, any, any, any)
//      there was one(battle).create(any)
//
//      result must beEqualTo(OkApiResult(RespondBattleRequestResult(ProfileModificationResult.OK, Some(u1.profile))))
//    }
  }
}

