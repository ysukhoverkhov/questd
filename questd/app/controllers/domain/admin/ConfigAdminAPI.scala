package controllers.domain.admin

import play.Logger

import models.store._
import models.domain.theme._
import models.domain.config._

import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._

import components._


case class GetConfigSectionRequest(name: String)
case class GetConfigSectionResult(section: Option[ConfigSection])

case class SetConfigSectionRequest(section: ConfigSection) 
case class SetConfigSectionResult() 


private [domain] trait ConfigAdminAPI { this: DBAccessor => 

  @volatile var config: Configuration = null
  
  private def readConfigFromDB(): Unit = {
    config = db.config.readConfig
    
    Logger.error("reading " + config.toString)
  }
  
  private def storeConfigInDB(section: ConfigSection): Unit = {

    db.config.upsertSection(section)
    
    Logger.error("saving " + config.toString)

    readConfigFromDB()
  }
  
  /**
   * Get config section by its name.
   */
  def getConfigSection(request: GetConfigSectionRequest): ApiResult[GetConfigSectionResult] = handleDbException {
    
    if (config == null)
      readConfigFromDB()
    
    OkApiResult(Some(GetConfigSectionResult(config(request.name))))
  }

  /**
   * Update config section
   */
  def setConfigSection(request: SetConfigSectionRequest) = handleDbException {
    storeConfigInDB(request.section)
    
    OkApiResult(Some(SetConfigSectionResult))
  }
  
  /*

  /**
   * List all themes
   */
  def allThemes: ApiResult[AllThemesResult] = handleDbException {
    Logger.debug("Admin request for all themes.")

    OkApiResult(Some(AllThemesResult(db.theme.allThemes)))
  }

*/
}


