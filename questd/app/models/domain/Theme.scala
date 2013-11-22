package models.domain

import models.domain.base.ID

case class Theme(
  val id: String = ID.generateUUID(),
  val text: String = "",
  val comment: String = "") extends ID

