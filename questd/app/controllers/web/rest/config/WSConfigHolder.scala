package controllers.web.rest.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

trait WSConfigHolder extends ConfigHolder { this: APIAccessor =>
  
  object ConfigParams {
    val MinAppVersion = "Min App Version" 
  }
  
  val configSectionName = "Web Service"
  val defaultConfiguration = ConfigSection(
    configSectionName,
    Map(ConfigParams.MinAppVersion -> "1"))
}

