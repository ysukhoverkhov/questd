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

  /**
   * Sets flag for unread messages for participants.
   */
  def setUnreadMessagesFlag(id: String, userId: String, flag: Boolean): Option[Conversation]

  /**
   * @param id Id of conversation to remove participant from.
   * @param userId Id of user to remove
   * @return Updated conversation.
   */
  def removeParticipant(id: String, userId: String): Option[Conversation]
}

