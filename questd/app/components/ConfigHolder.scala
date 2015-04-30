package components

import models.domain.admin._
import controllers.domain._
import controllers.domain.admin._

trait ConfigHolder { this: APIAccessor =>

  /**
   *  Name of config section we use to store our configuration.
   */
  def configSectionName: String

  /**
   * Default configuration if empty or should be reset.
   */
  def defaultConfiguration: ConfigSection

  /**
   * Read values from configuration.
   */
  def config: ConfigSection = {
    api.getConfigSection(GetConfigSectionRequest(configSectionName)) match {
      case OkApiResult(GetConfigSectionResult(Some(c: ConfigSection))) => {
        if (c.values.keySet == defaultConfiguration.values.keySet)
          c
        else {
          resetConfig()
          defaultConfiguration
        }
      }
      case _ => {
        resetConfig()
        defaultConfiguration
      }
    }
  }

  /**
   * Updates a field in configuration section.
   */
  def updateConfig(field: (String, String)): Unit = {
    api.setConfigSection(SetConfigSectionRequest(config.copy(values = config.values + field)))
  }

  /**
   * Resets component's configuration to default one.
   */
  def resetConfig(): Unit = {
    api.setConfigSection(SetConfigSectionRequest(defaultConfiguration))
  }

  /**
   * Check if config section is not initialized and initialize it.
   */
  private def initConfiguration(): Unit = config
}

