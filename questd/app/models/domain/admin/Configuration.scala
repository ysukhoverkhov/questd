package models.domain.admin

import models.domain.base.ID

case class Configuration(
  private val _sections: Map[String, ConfigSection]) {

  def replaceSection(newSection: ConfigSection) = {
    Configuration(_sections.updated(newSection.id, newSection))
  }

  def apply(name: String): Option[ConfigSection] = {
    _sections.get(name)
  }

  def sections: List[ConfigSection] = _sections.values.toList
}

case class ConfigSection(
  val id: String,
  val values: Map[String, String]) extends ID

