package models.domain

import models.domain.base.ID
import java.util.Date

case class Theme(
  val id: String = ID.generateUUID(),
  val icon: ContentReference = ContentReference(ContentType.Photo, "url", "http://upload.wikimedia.org/wikipedia/ru/b/b3/Iconlogo.gif"),
  val text: String,
  val comment: String,
  val lastUseDate: Date = new Date(0)) extends ID

