package models.domain

import scala.language.implicitConversions

import models.domain.base._
import models.domain.profile._

object theme {

  implicit def stringToID(stringId: String): ThemeID = { ThemeID(stringId) }

  case class ThemeID(id: String) extends BaseID[String]
  object ThemeID {
    val default = ThemeID("")
  }

  case class Theme(
    val id: ThemeID,
    val text: String,
    val comment: String) {

    def replaceID(newID: ThemeID) = {
      Theme(newID, text, comment)
    }
  }

  object Theme {
    def apply(id: ThemeID): Theme = {
      Theme(id, "", "")
    }

  }

}

