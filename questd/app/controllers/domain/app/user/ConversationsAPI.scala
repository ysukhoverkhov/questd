package controllers.domain.app.user

import java.util.Date

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain.chat.{ChatMessage, Conversation, Participant}
import models.domain.user._
import models.domain.user.message.{MessageNewChatMessage, MessageType}

case class CreateConversationRequest(user: User, peerId: String)
case class CreateConversationResult(allowed: ProfileModificationResult, conversationId: Option[String] = None)

case class LeaveConversationRequest(user: User, conversationId: String)
case class LeaveConversationResult(allowed: ProfileModificationResult)

case class GetMyConversationsRequest(user: User)
case class GetMyConversationsResult(conversations: List[Conversation])

case class SendChatMessageRequest(user: User, conversationId: String, message: String)
case class SendChatMessageResult(allowed: ProfileModificationResult)

case class GetChatMessagesRequest(user: User, conversationId: String, fromDate: Date, count: Int)
case class GetChatMessagesResult(allowed: ProfileModificationResult, messages: Option[List[ChatMessage]] = None)


private[domain] trait ConversationsAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Creates or reuses conversation with a user.
   */
  def createConversation(request: CreateConversationRequest): ApiResult[CreateConversationResult] = handleDbException {
    import request._

    db.conversation.findByAllParticipants(List(user.id, peerId)).toList.headOption.fold {
      db.user.readById(peerId).fold {
        OkApiResult(CreateConversationResult(OutOfContent))
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
    import request._

    db.conversation.readById(conversationId).fold {
      OkApiResult(LeaveConversationResult(OutOfContent))
    } { conversation =>

      if (conversation.participants.exists(_.userId == user.id)) {
        db.conversation.removeParticipant(conversation.id, user.id)
        OkApiResult(LeaveConversationResult(OK))
      } else {
        OkApiResult(LeaveConversationResult(OutOfContent))
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
    import request._

    val maxMessageLength = api.config(api.DefaultConfigParams.ChatMaxMessageLength).toInt

    if (message.length > maxMessageLength) {
      OkApiResult(SendChatMessageResult(LimitExceeded))
    } else {

      db.conversation.readById(conversationId).fold {
        OkApiResult(SendChatMessageResult(OutOfContent))
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
    val count = adjustedPageSize(request.count)

    db.conversation.readById(request.conversationId).fold {
      OkApiResult(GetChatMessagesResult(
        OutOfContent))
    } { conversation =>

      db.conversation.setUnreadMessagesFlag(conversation.id, request.user.id, flag = false)

      OkApiResult(GetChatMessagesResult(
        OK,
        Some(db.chat.getForConversation(request.conversationId, request.fromDate).take(count).toList)))
    }
  }
}

