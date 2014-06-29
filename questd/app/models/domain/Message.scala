package models.domain

import models.domain.base.ID

/**
 * A message to be displayed to user.
 */
case class Message (
    id: String = ID.generateUUID(),
    icon: Option[ContentReference] = None,
    text: String) extends ID
    