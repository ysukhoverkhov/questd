package models.domain

case class AuthInfo(
  session: Option[SessionID] = None,
  fbid: Option[String] = None)

