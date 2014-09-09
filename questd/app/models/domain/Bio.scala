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
  gender: Gender.Value = Gender.Unknown,
  timezone: Int = 0,
  country: String = "",
  city: String = "")
    