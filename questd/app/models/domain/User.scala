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

// TODO IMPLEMENT: wrap app auth elated things into Auth field.
case class User(
  val id: UserID,
  val fbid: Option[String] = None,
  val session: Option[SessionID] = None,
  val profile: Profile = Profile(),
  val questProposalContext: QuestProposalConext = QuestProposalConext()) {

  def replaceSessionID(newID: SessionID) = {
    User(id, fbid, Some(newID), profile, questProposalContext)
  }
}

object User {

} 


