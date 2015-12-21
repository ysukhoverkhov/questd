package controllers.domain.app.user

import java.util.Date

import controllers.domain._
import models.domain.chat.{ChatMessage, Conversation, Participant}
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

    "createConversation does not create conversation with not existing user" in context {
      val u = createUserStub()

      db.conversation.findByAllParticipants(any) returns Iterator.empty
      db.user.readById(any) returns None

      val result = api.createConversation(CreateConversationRequest(u, "1"))

      there was one(conversation).findByAllParticipants(any)
      there was one(user).readById(any)
      there were no(conversation).create(any)

      result must beEqualTo(OkApiResult(CreateConversationResult(CreateConversationCode.PeerNotFount)))
    }

    "createConversation creates new conversation" in context {
      val u = createUserStub()
      val peer = createUserStub()

      db.conversation.findByAllParticipants(any) returns Iterator.empty
      db.user.readById(any) returns Some(peer)

      val result = api.createConversation(CreateConversationRequest(u, "1"))

      there was one(conversation).findByAllParticipants(any)
      there was one(user).readById(any)
      there was one(conversation).create(any)

      result must beAnInstanceOf[OkApiResult[CreateConversationResult]]
    }

    "createConversation does not create conversation with banned peer" in context {
      val peer = createUserStub()
      val u = createUserStub(banned = List(peer.id))

      db.conversation.findByAllParticipants(any) returns Iterator.empty
      db.user.readById(any) returns Some(peer)

      val result = api.createConversation(CreateConversationRequest(u, "1"))

      there was one(conversation).findByAllParticipants(any)
      there was one(user).readById(any)
      there was no(conversation).create(any)

      result must beAnInstanceOf[OkApiResult[CreateConversationResult]]
    }

    "createConversation does not create conversation if we are banned" in context {
      val u = createUserStub()
      val peer = createUserStub(banned = List(u.id))

      db.conversation.findByAllParticipants(any) returns Iterator.empty
      db.user.readById(any) returns Some(peer)

      val result = api.createConversation(CreateConversationRequest(u, "1"))

      there was one(conversation).findByAllParticipants(any)
      there was one(user).readById(any)
      there was no(conversation).create(any)

      result must beAnInstanceOf[OkApiResult[CreateConversationResult]]
    }

    "Do not accept too long messages" in context {
      val u = createUserStub()

      val result = api.sendChatMessage(SendChatMessageRequest(
        user = u,
        conversationId = "",
        message = (1 to 10000).toList.mkString))

      result must beEqualTo(OkApiResult(SendChatMessageResult(SendChatMessageCode.MessageLengthLimitExceeded)))
    }

    "Do not accept message for not existing conversation" in context {
      val pIds = List("1", "2")
      val u = createUserStub(id = pIds(0))
      val conv = createConversationStub(pIds = pIds)

      conversation.readById(any) returns None
      user.readById(any) returns Some(u)
      doReturn(OkApiResult(SendMessageResult(u))).when(api).sendMessage(any)

      val result = api.sendChatMessage(SendChatMessageRequest(
        user = u,
        conversationId = "",
        message = ""))

      there was one (conversation).readById(any)
      there was no (chat).create(any)
      there was no (conversation).setUnreadMessagesFlag(conv.id, pIds(1), flag = true)
      there was no (api).sendMessage(any)

      result must beEqualTo(OkApiResult(SendChatMessageResult(SendChatMessageCode.ConversationNotFound)))
    }

    "getChatMessages does not return messages for not existing conversation" in context {
      conversation.readById(any) returns None

      val result = api.getChatMessages(GetChatMessagesRequest(createUserStub(), "", new Date(0), 10))

      there was one (conversation).readById(any)
      result must beEqualTo(OkApiResult(GetChatMessagesResult(GetChatMessagesCode.ConversationNotFound)))
    }

    "getChatMessages returns messages and resets unread flag" in context {
      val c = createConversationStub()

      conversation.readById(any) returns Some(c)
      conversation.setUnreadMessagesFlag(any, any, any) returns Some(c)
      chat.getForConversation(any, any) returns (1 to 10).map(i => ChatMessage("", "", "", "")).iterator

      val result = api.getChatMessages(GetChatMessagesRequest(createUserStub(), "", new Date(0), 10))

      there was one (conversation).readById(any)
      there was one (conversation).setUnreadMessagesFlag(any, any, any)
      there was one (chat).getForConversation(any, any)
      result must beAnInstanceOf[OkApiResult[GetChatMessagesResult]]
    }

    "give ability to leave conversation" in context {
      val c = createConversationStub()

      db.conversation.readById(any) returns Some(c)
      db.conversation.removeParticipant(any, any)

      val result = api.leaveConversation(LeaveConversationRequest(createUserStub(), c.id))

      there was one (conversation).readById(any)
      there was one (conversation).removeParticipant(any, any)
      result must beAnInstanceOf[OkApiResult[GetChatMessagesResult]]
    }
  }
}

