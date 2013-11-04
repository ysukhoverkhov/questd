package components

import models.domain.config._

trait ConfigHolder {
  
  // Name of config section we use to store our configuration.
  def configSectionName: String
  
  def defaultConfiguration: ConfigSection
  
  val configuration: ConfigSection = {
    // TODO IMPLEMENT read me from database. If no db config found store it there.
    
    defaultConfiguration
    
  }
  
}

