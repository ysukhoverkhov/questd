package models.domain

case class AuthInfo(
  session: Option[String] = None,
  fbid: Option[String] = None)

