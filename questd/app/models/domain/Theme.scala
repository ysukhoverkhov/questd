package models.domain

import models.domain.base._

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


