package components

import models.domain.config._

trait ConfigHolder {
  
  // Name of config section we use to store our configuration.
  def configSectionName: String
  
  def defaultConfiguration: ConfigSection
  
  def config: ConfigSection = {
    // TODO IMPLEMENT read me from database. If no db config found store it there.
    
    // TODO IMPLEMENT read it not from db, but from api what will cache it
    // save to API as well.
    
    defaultConfiguration
    
  }
  
}

