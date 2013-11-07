package models

import scala.language.implicitConversions

package object domain {
  implicit def string2ID(stringId: String): ThemeID = ThemeID(stringId)
  implicit def ID2String(id: ThemeID): String = id.toString

  implicit def string2UserID(stringId: String): UserID = UserID(stringId)
  implicit def UserID2String(id: UserID): String = id.toString

  implicit def stringToSessionID(stringId: String): SessionID = { SessionID(stringId) }

}