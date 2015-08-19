package models.store.dao

import models.domain.chat.Conversation

trait ConversationDAO extends BaseDAO[Conversation] {

  /**
   * Searches culture by containing country.
   */
  def findByParticipant(participantId: String): Iterator[Conversation]
}

