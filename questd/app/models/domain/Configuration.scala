package models.domain

object config {

  case class Configuration(
    private val sections: Map[String, ConfigSection]) {
    
    def replaceSection(newSection: ConfigSection) = {
      Configuration(sections.updated(newSection.name, newSection))
    }
    
    def apply(name: String): Option[ConfigSection] = {
      sections.get(name)
    }
  }

  case class ConfigSection(
    val name: String,
    val values: Map[String, String])
}

