package models.domain

case class Theme(
  val id: String,
  val text: String = "",
  val comment: String = "") extends ID

