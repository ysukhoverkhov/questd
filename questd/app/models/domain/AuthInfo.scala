package models.domain

import java.util.Date

case class AuthInfo(
  session: Option[String] = None,
  fbid: Option[String] = None,
  fbtoken: Option[String] = None, 
  lastLogin: Option[Date] = None)

