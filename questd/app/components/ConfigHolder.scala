package components

import models.domain.admin._
import controllers.domain._
import controllers.domain.admin._

trait ConfigHolder { this: APIAccessor =>

  /**
   *  Name of config section we use to store our configuration.
   */
  protected def defaultSectionName: String

  /**
   * Default configuration if empty or should be reset.
   */
  protected def defaultConfiguration: Map[String, ConfigSection]


  /**
   * Read values from configuration.
   */
  def config: ConfigSection = configNamed(defaultSectionName)

  /**
   * Read values from configuration.
   */
  def configNamed(sectionName: String): ConfigSection = {
    api.getConfigSection(GetConfigSectionRequest(sectionName)) match {
      case OkApiResult(GetConfigSectionResult(Some(c: ConfigSection))) =>
        if (c.values.keySet == defaultConfiguration(sectionName).values.keySet)
          c
        else {
          resetConfigSection(sectionName)
          defaultConfiguration(sectionName)
        }
      case _ =>
        resetConfigSection(sectionName)
        defaultConfiguration(sectionName)
    }
  }

  /**
   * Updates a field in configuration section.
   */
  // TODO: use it.
//  def updateConfig(field: (String, String)): Unit = {
//    api.setConfigSection(SetConfigSectionRequest(config.copy(values = config.values + field)))
//  }

  /**
   * Resets component's configuration to default one.
   */
  private def resetConfigSection(sectionName: String): Unit = {
    api.setConfigSection(SetConfigSectionRequest(defaultConfiguration(sectionName)))
  }
}

