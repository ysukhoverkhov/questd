package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain.chat.{Conversation, Participant}
import models.domain.user._

case class CreateConversationRequest(user: User, peerId: String)
case class CreateConversationResult(allowed: ProfileModificationResult, conversationId: Option[String] = None)

case class GetMyConversationsRequest(user: User)
case class GetMyConversationsResult(conversations: List[Conversation])

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
}

