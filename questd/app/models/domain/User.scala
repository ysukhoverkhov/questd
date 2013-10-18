package models.domain

import scala.language.implicitConversions

import models.domain.auth._
import models.domain.profile._

object user {

  implicit def stringToUserID(stringId: String): UserID = { UserID(stringId) }

  case class UserID(id: String) 
  object UserID {
    val default = UserID("")
  }

  case class User(
    val id: UserID,
    val username: String,
    val password: String,

    val profile: Profile)
    
  object User {
    
    def apply(id: UserID, username: String, password: String): User = {
      User(id, username, password, Profile.default)
    }

    def apply(username: String, password: String): User = {
      User(UserID.default, username, password, Profile.default)
    }
  } 
}

