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

//case class AllThemesResult(themes: List[Theme])
//


private [domain] trait ConfigAdminAPI { this: DBAccessor => 

  @volatile var config: Configuration = null
  
  private def readConfigFromDB(): Unit = {
    
    // TODO implement me (remove the if).
    if (config == null)
    config = Configuration(Map())
    
    Logger.error("reading " + config.toString)
  }
  
  private def storeConfigInDB(newConfig: Configuration): Unit = {
    
    // TODO implement me. (do not modify local variable, just store new values in db.)
    config = newConfig

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

  def setConfigSection(request: SetConfigSectionRequest) = handleDbException {
    val newConfig = config.replaceSection(request.section)
    storeConfigInDB(newConfig)
    
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


