package models

import scala.language.implicitConversions
import models.domain.base.BaseID

package object domain {

  implicit def ID2String[T <: BaseID[String]](id: T): String = id.toString


  implicit def string2ID(stringId: String): ThemeID = ThemeID(stringId)

  implicit def string2UserID(stringId: String): UserID = UserID(stringId)

  implicit def stringToSessionID(stringId: String): SessionID = { SessionID(stringId) }

}
