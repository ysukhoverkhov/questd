package models.store.dao

import models.domain.chat.Conversation

trait ConversationDAO extends BaseDAO[Conversation] {

  /**
   * Searches conversation containing the participant.
   */
  def findByParticipant(participantId: String): Iterator[Conversation]

  /**
   * Searches conversation containing all participants.
   */
  def findByAllParticipants(participantIds: Seq[String]): Iterator[Conversation]
}

