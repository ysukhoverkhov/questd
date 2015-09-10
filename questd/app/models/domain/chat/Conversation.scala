package models.domain.chat

import models.domain.base.ID

/**
 * Conversation by two users.
 *
 * Created by Yury on 03.08.2015.
 */
case class Conversation (
  id: String = ID.generate,
  participants: List[Participant]
  ) extends ID
