package models.domain

import models.domain.base.ID

case class Theme(
  val id: String = ID.generateUUID(),
  val icon: ContentReference = ContentReference("Photo", "url", "http://upload.wikimedia.org/wikipedia/ru/b/b3/Iconlogo.gif"),
  val text: String = "",
  val comment: String = "") extends ID

