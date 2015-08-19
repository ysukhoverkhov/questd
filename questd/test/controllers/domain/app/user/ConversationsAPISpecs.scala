package controllers.domain.app.user

import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult
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

    "createConversation returns existing conversation" in context {
      val u = createUserStub()
      val c = createConversationStub(pIds = List(u.id))

      db.conversation.findByAllParticipants(any) returns List(c).iterator

      val result = api.createConversation(CreateConversationRequest(u, "1"))

      there was one(conversation).findByAllParticipants(any)
      there were no(user).readById(any)
      there were no(conversation).create(any)

      result must beAnInstanceOf[OkApiResult[CreateConversationResult]]
    }

    "createConversation does not create conversation with nonexisting user" in context {
      val u = createUserStub()
      val c = createConversationStub(pIds = List(u.id))

      db.conversation.findByAllParticipants(any) returns Iterator.empty
      db.user.readById(any) returns None

      val result = api.createConversation(CreateConversationRequest(u, "1"))

      there was one(conversation).findByAllParticipants(any)
      there was one(user).readById(any)
      there were no(conversation).create(any)

      result must beEqualTo(OkApiResult(CreateConversationResult(ProfileModificationResult.OutOfContent)))
    }

    "createConversation creates new conversation" in context {
      val u = createUserStub()
      val peer = createUserStub()
      val c = createConversationStub(pIds = List(u.id, peer.id))

      db.conversation.findByAllParticipants(any) returns Iterator.empty
      db.user.readById(any) returns Some(peer)

      val result = api.createConversation(CreateConversationRequest(u, "1"))

      there was one(conversation).findByAllParticipants(any)
      there was one(user).readById(any)
      there was one(conversation).create(any)

      result must beAnInstanceOf[OkApiResult[CreateConversationResult]]
    }

  }
}

