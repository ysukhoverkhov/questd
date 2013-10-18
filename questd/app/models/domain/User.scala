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
    val password: String,
    val session: SessionID,
    val profile: Profile) {
    
    def replaceSessionID(newID: SessionID) = {
      User(id, username, password, newID, profile)
    }
  }
    
  object User {

    def apply(id: UserID, username: String, password: String, session: SessionID): User = {
      User(id, username, password, session, Profile.default)
    }

    def apply(id: UserID, username: String, password: String): User = {
      User(id, username, password, SessionID.default, Profile.default)
    }

    def apply(username: String, password: String): User = {
      User(UserID.default, username, password, SessionID.default, Profile.default)
    }
  } 
}

