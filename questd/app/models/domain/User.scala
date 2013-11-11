package models.domain

import scala.language.implicitConversions

import models.domain.base._

case class UserID(id: String) extends BaseID[String]
object UserID {
  val default = UserID("")
}

case class SessionID(id: String) extends BaseID[String]
object SessionID {
  val default = SessionID("")
}


case class User(
  id: UserID,
  auth: AuthInfo = AuthInfo(),
  profile: Profile = Profile(Assets(0, 0, 0)),
  questProposalContext: QuestProposalConext = QuestProposalConext()) {
}

