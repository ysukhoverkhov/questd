package components

import models.domain.config._
import controllers.domain._
import controllers.domain.admin._

trait ConfigHolder { this: APIAccessor =>
  
  // Name of config section we use to store our configuration.
  def configSectionName: String
  
  def defaultConfiguration: ConfigSection
  
  def config: ConfigSection = {
    api.getConfigSection(GetConfigSectionRequest(configSectionName)) match {
      case OkApiResult(Some(GetConfigSectionResult(Some(c: ConfigSection)))) => c
      case _ => {
        api.setConfigSection(SetConfigSectionRequest(defaultConfiguration))
        defaultConfiguration
      }
    } 
  }
  
}

