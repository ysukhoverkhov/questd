package controllers.domain.admin

import components._
import controllers.domain._
import controllers.domain.helpers._
import models.domain.admin._

case class GetConfigSectionRequest(name: String)
case class GetConfigSectionResult(section: Option[ConfigSection])

case class SetConfigSectionRequest(section: ConfigSection)
case class SetConfigSectionResult()

case class GetConfigurationRequest()
case class GetConfigurationResult(config: Configuration)

private[domain] trait ConfigAdminAPI { this: DBAccessor =>

  /**
   * Get config section by its name.
   */
  def getConfigSection(request: GetConfigSectionRequest): ApiResult[GetConfigSectionResult] = handleDbException {
    OkApiResult(GetConfigSectionResult(db.config.readConfig(request.name)))
  }

  /**
   * Get entire configuration.
   */
  def getConfiguration(request: GetConfigurationRequest): ApiResult[GetConfigurationResult] = handleDbException {
    OkApiResult(GetConfigurationResult(db.config.readConfig))
  }

  /**
   * Update config section
   */
  def setConfigSection(request: SetConfigSectionRequest) = handleDbException {
    db.config.upsert(request.section)

    OkApiResult(SetConfigSectionResult)
  }

}


