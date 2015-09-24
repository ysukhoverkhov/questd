package models.domain.user.auth

import java.util.Date

/**
 * User's information related to authorizarion.
 */
case class AuthInfo(
  session: Option[String] = None,
  loginMethods: List[LoginMethod] = List.empty,
  lastLogin: Option[Date] = None)
