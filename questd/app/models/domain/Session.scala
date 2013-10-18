package models.domain

import scala.language.implicitConversions
import models.domain.user._


object auth {

  implicit def stringToSessionID(stringId: String): SessionID = { SessionID(stringId) }

  case class SessionID(id: String) {
     override def toString = id
  }

  case class Session(id: SessionID, user: User)
  
  object Session {

    def apply(user: User): Session = {
      apply("", user)
    }
  }

}




