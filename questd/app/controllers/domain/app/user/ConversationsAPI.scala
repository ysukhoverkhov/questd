package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain.chat.{ChatMessage, Conversation, Participant}
import models.domain.user._
import models.domain.user.message.{MessageNewChatMessage, MessageType}

case class CreateConversationRequest(user: User, peerId: String)
case class CreateConversationResult(allowed: ProfileModificationResult, conversationId: Option[String] = None)

case class GetMyConversationsRequest(user: User)
case class GetMyConversationsResult(conversations: List[Conversation])

case class SendChatMessageRequest(user: User, conversationId: String, message: String)
case class SendChatMessageResult(allowed: ProfileModificationResult)


private[domain] trait ConversationsAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Creates or reuses conversation with a user.
   */
  def createConversation(request: CreateConversationRequest): ApiResult[CreateConversationResult] = handleDbException {
    import request._
    // TODO: test me - it reuses if finds existing or creates new one in other case.

    db.conversation.findByAllParticipants(List(user.id, peerId)).toList.headOption.fold {
      // TODO: check here peer exists. // TODO: test it.
      db.user.readById(peerId).fold {
        OkApiResult(CreateConversationResult(OutOfContent))
      } {
        peer =>
          val newConversation = Conversation(
            participants = List(user.id, peer.id).map(Participant(_))
          )
          db.conversation.create(newConversation)

          OkApiResult(CreateConversationResult(OK,  Some(newConversation.id)))
      }
    } { c =>
      OkApiResult(CreateConversationResult(OK,  Some(c.id)))
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

    val maxMessageLength = api.config(api.DefaultConfigParams.DefaultCultureId).toInt

    if (message.length > maxMessageLength) { // TODO: test this.
      OkApiResult(SendChatMessageResult(LimitExceeded))
    } else {
      val maybeConversation = db.conversation.readById(conversationId)

      maybeConversation.fold { // TODO: test this.
        OkApiResult(SendChatMessageResult(OutOfContent))
      } { conversation => // TODO: test this.
        db.chat.create(ChatMessage(
          senderId = user.id,
          conversationId = conversation.id,
          message = message
        ))

        conversation.participants.filterNot(_.userId == user.id).foreach { participant =>
          if (!participant.hasUnreadMessages) { // TODO: test this.
            db.conversation.setUnreadMessagesFlag(conversation.id, participant.userId, flag = true)
          }

          db.user.readById(participant.userId).fold(){ participantUser => // TODO: test this.
            if (!participantUser.profile.messages.exists(_.messageType == MessageType.NewChatMessage)) {
              sendMessage(SendMessageRequest(participantUser, MessageNewChatMessage()))
            }
          }
        }

        OkApiResult(SendChatMessageResult(OK))
      }
    }
  }
}

