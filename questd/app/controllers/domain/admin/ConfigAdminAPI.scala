package controllers.domain.admin

import play.Logger

import models.store._
import models.domain.admin._

import controllers.domain._
import controllers.domain.helpers._

import components._

case class GetConfigSectionRequest(name: String)
case class GetConfigSectionResult(section: Option[ConfigSection])

case class SetConfigSectionRequest(section: ConfigSection)
case class SetConfigSectionResult()

case class GetConfigurationRequest()
case class GetConfigurationResult(config: Configuration)

private[domain] trait ConfigAdminAPI { this: DBAccessor =>

  @volatile var adminConfig: Configuration = null

  private def storeConfigInDB(section: ConfigSection): Unit = {
    db.config.upsert(section)

    adminConfig = db.config.readConfig
  }
  
  private def checkInit(): Unit = {
    if (adminConfig == null)
      adminConfig = db.config.readConfig
  }

  /**
   * Get config section by its name.
   */
  def getConfigSection(request: GetConfigSectionRequest): ApiResult[GetConfigSectionResult] = handleDbException {
    checkInit()

    OkApiResult(Some(GetConfigSectionResult(adminConfig(request.name))))
  }

  /**
   * Get entire configuration.
   */
  def getConfiguration(request: GetConfigurationRequest): ApiResult[GetConfigurationResult] = handleDbException {
    checkInit()
    OkApiResult(Some(GetConfigurationResult(adminConfig)))
  }

  /**
   * Update config section
   */
  def setConfigSection(request: SetConfigSectionRequest) = handleDbException {
    storeConfigInDB(request.section)

    OkApiResult(Some(SetConfigSectionResult))
  }

}


