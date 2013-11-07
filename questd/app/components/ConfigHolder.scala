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

  def config: ConfigSection = {
    api.getConfigSection(GetConfigSectionRequest(configSectionName)) match {
      case OkApiResult(Some(GetConfigSectionResult(Some(c: ConfigSection)))) => c
      case _ => resetConfig
    }
  }

  /**
   * Resets component's configuration to default one.
   */
  def resetConfig = {
    api.setConfigSection(SetConfigSectionRequest(defaultConfiguration))
    defaultConfiguration
  }

}

