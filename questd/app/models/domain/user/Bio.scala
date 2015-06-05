package models.domain.user

import models.domain.common.ContentReference

/**
 * These objects hold public personalized information.
 */
case class Bio(
  avatar: Option[ContentReference] = None,
  name: String = "",
  gender: Gender.Value = Gender.Unknown,
  timezone: Int = 0,
  country: Option[String] = None,
  city: Option[String] = None)
