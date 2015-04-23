package models.domain

import java.util.Date

case class AuthInfo(
  session: Option[String] = None,
  snids: Map[String, String] = Map.empty,
  lastLogin: Option[Date] = None)

