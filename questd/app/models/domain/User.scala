package models.domain

import scala.language.implicitConversions

import models.domain.base._

case class UserID(id: String = "") extends BaseID[String]
case class SessionID(id: String = "") extends BaseID[String]

case class User(
  id: UserID,
  auth: AuthInfo = AuthInfo(),
  profile: Profile = Profile()) {
}

