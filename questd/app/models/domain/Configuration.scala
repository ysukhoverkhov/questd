package models.domain

object config {

  case class Configuration(
    val sections: Map[String, ConfigSection])

  case class ConfigSection(
    val name: String,
    val values: Map[String, String])
}

