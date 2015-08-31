package models.store.dao

import java.util.Date

import models.domain.chat.ChatMessage

trait ChatMessageDAO extends BaseDAO[ChatMessage] {

  /**
   * Gets all messages in conversation since date and with limit.
   */
  def getForConversation(
    conversationId: String,
    since: Date): Iterator[ChatMessage]

}

