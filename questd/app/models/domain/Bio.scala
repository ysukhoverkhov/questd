package models.domain

object Gender extends Enumeration {
  val Male, Female, Unknown = Value
}

/**
 * These objects hold public personalized information.
 */
case class Bio(
  avatar: Option[ContentReference] = None,
  name: String = "",
  gender: String = Gender.Unknown.toString,
  timezone: Int = 0)
    