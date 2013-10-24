package models.domain

import scala.language.implicitConversions

import models.domain.base._
import models.domain.profile._

object user {

  implicit def stringToUserID(stringId: String): UserID = { UserID(stringId) }

  case class UserID(id: String) extends BaseID[String]
  object UserID {
    val default = UserID("")
  }

  
  implicit def stringToSessionID(stringId: String): SessionID = { SessionID(stringId) }

  case class SessionID(id: String) extends BaseID[String]
  object SessionID {
    val default = SessionID("")
  }
  
  case class User(
    val id: UserID,
    val fbid: Option[String] = None,
    val session: Option[SessionID] = None,
    val profile: Profile) {
    
    def replaceSessionID(newID: SessionID) = {
      User(id, fbid, Some(newID), profile)
    }
  }
    
  object User {

    def apply(id: UserID): User = {
      User(id, None, None, Profile.default)
    }

    def apply(id: UserID, fbid: String): User = {
      User(id, Some(fbid), None, Profile.default)
    }

    def apply(id: UserID, fbid: Option[String], session: Option[SessionID]): User = {
      User(id, fbid, session, Profile.default)
    }

  } 
}

