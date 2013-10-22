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
    val username: String,
    val fbid: String,
    val session: SessionID,
    val profile: Profile) {
    
    def replaceSessionID(newID: SessionID) = {
      User(id, username, fbid, newID, profile)
    }
  }
    
  object User {

    def apply(id: UserID, username: String, fbid: String, session: SessionID): User = {
      User(id, username, fbid, session, Profile.default)
    }

    def apply(id: UserID, username: String, fbid: String): User = {
      User(id, username, fbid, SessionID.default, Profile.default)
    }

    def apply(username: String, fbid: String): User = {
      User(UserID.default, username, fbid, SessionID.default, Profile.default)
    }

    def apply(fbid: String): User = {
      User(UserID.default, "", fbid, SessionID.default, Profile.default)
    }
  } 
}

