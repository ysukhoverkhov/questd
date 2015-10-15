package controllers.domain.app.user

import java.util.Date

import components._
import controllers.domain._
import controllers.domain.app.protocol.CommonCode
import controllers.domain.helpers._
import models.domain.chat.{ChatMessage, Conversation, Participant}
import models.domain.user._
import models.domain.user.message.{MessageNewChatMessage, MessageType}

object CreateConversationCode extends Enumeration  with CommonCode {
  val PeerNotFount = Value
  val PeerBanned = Value
  val UserBanned = Value
}
case class CreateConversationRequest(user: User, peerId: String)
case class CreateConversationResult(allowed: CreateConversationCode.Value, conversationId: Option[String] = None)

object LeaveConversationCode extends Enumeration with CommonCode {
  val ConversationNotFound = Value
  val NotInConversation = Value
}
case class LeaveConversationRequest(user: User, conversationId: String)
case class LeaveConversationResult(allowed: LeaveConversationCode.Value)

case class GetMyConversationsRequest(user: User)
case class GetMyConversationsResult(conversations: List[Conversation])

object SendChatMessageCode extends Enumeration with CommonCode  {
  val ConversationNotFound = Value
  val MessageLengthLimitExceeded = Value
}
case class SendChatMessageRequest(user: User, conversationId: String, message: String)
case class SendChatMessageResult(allowed: SendChatMessageCode.Value)

object GetChatMessagesCode extends Enumeration with CommonCode  {
  val ConversationNotFound = Value
}
case class GetChatMessagesRequest(user: User, conversationId: String, fromDate: Date, count: Int)
case class GetChatMessagesResult(allowed: GetChatMessagesCode.Value, messages: Option[List[ChatMessage]] = None)


private[domain] trait ConversationsAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Creates or reuses conversation with a user.
   */
  def createConversation(request: CreateConversationRequest): ApiResult[CreateConversationResult] = handleDbException {
    import CreateConversationCode._
    import request._

    db.conversation.findByAllParticipants(List(user.id, peerId)).toList.headOption.fold {
      db.user.readById(peerId).fold {
        OkApiResult(CreateConversationResult(PeerNotFount))
      } {
        peer =>
          user.canConversateWith(peer) match {
            case OK =>
              val newConversation = Conversation(
                participants = List(user.id, peer.id).map(Participant(_))
              )
              db.conversation.create(newConversation)

              OkApiResult(CreateConversationResult(OK,  Some(newConversation.id)))

            case result =>
              OkApiResult(CreateConversationResult(result))
          }
      }
    } { c =>
      OkApiResult(CreateConversationResult(OK,  Some(c.id)))
    }
  }

  /**
   * Leaves a conversation. If conversation has no participants destroys it.
   */
  def leaveConversation(request: LeaveConversationRequest): ApiResult[LeaveConversationResult] = handleDbException {
    import LeaveConversationCode._
    import request._

    db.conversation.readById(conversationId).fold {
      OkApiResult(LeaveConversationResult(ConversationNotFound))
    } { conversation =>

      if (conversation.participants.exists(_.userId == user.id)) {
        db.conversation.removeParticipant(conversation.id, user.id)
        OkApiResult(LeaveConversationResult(OK))
      } else {
        OkApiResult(LeaveConversationResult(NotInConversation))
      }
    }
  }

  /**
   * Returns all our conversations.
   */
  def getMyConversations(request: GetMyConversationsRequest): ApiResult[GetMyConversationsResult] = handleDbException {
    import request._

    OkApiResult(GetMyConversationsResult(
      db.conversation.findByParticipant(user.id).toList
    ))
  }

  /**
   * Adds new message to conversation.
   */
  def sendChatMessage(request: SendChatMessageRequest): ApiResult[SendChatMessageResult] = handleDbException {
    import SendChatMessageCode._
    import request._

    val maxMessageLength = api.config(api.DefaultConfigParams.ChatMaxMessageLength).toInt

    if (message.length > maxMessageLength) {
      OkApiResult(SendChatMessageResult(MessageLengthLimitExceeded))
    } else {

      db.conversation.readById(conversationId).fold {
        OkApiResult(SendChatMessageResult(ConversationNotFound))
      } { conversation =>
        db.chat.create(ChatMessage(
          senderId = user.id,
          conversationId = conversation.id,
          message = message
        ))

        conversation.participants.filterNot(_.userId == user.id).foreach { participant =>
          if (!participant.hasUnreadMessages) {
            db.conversation.setUnreadMessagesFlag(conversation.id, participant.userId, flag = true)
          }

          db.user.readById(participant.userId).fold(){ participantUser =>
            if (!participantUser.profile.messages.exists(_.messageType == MessageType.NewChatMessage)) {
              sendMessage(SendMessageRequest(participantUser, MessageNewChatMessage()))
            }
          }
        }

        OkApiResult(SendChatMessageResult(OK))
      }
    }
  }

  /**
   * Adds new message to conversation.
   */
  def getChatMessages(request: GetChatMessagesRequest): ApiResult[GetChatMessagesResult] = handleDbException {
    import GetChatMessagesCode._

    val count = adjustedPageSize(request.count)

    db.conversation.readById(request.conversationId).fold {
      OkApiResult(GetChatMessagesResult(ConversationNotFound))
    } { conversation =>

      db.conversation.setUnreadMessagesFlag(conversation.id, request.user.id, flag = false)

      OkApiResult(GetChatMessagesResult(
        OK,
        Some(db.chat.getForConversation(request.conversationId, request.fromDate).take(count).toList)))
    }
  }
}

