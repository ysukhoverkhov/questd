package models.domain

/**
 * These objects hold public personalized information.
 */
case class Bio (
    avatar: Option[ContentReference] = None,
    name: String = "",
    timezone: Int = 0)