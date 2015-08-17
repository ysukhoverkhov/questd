package models.domain.chat

import models.domain.base.ID

/**
 * Conversation by two users.
 *
 * Created by Yury on 03.08.2015.
 */
case class ChatConversation (
  id: String = ID.generateUUID(),
  participants: List[ChatParticipant]
  ) extends ID
