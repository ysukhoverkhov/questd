package controllers.web.rest.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

trait WSConfigHolder extends ConfigHolder { this: APIAccessor =>

  object ConfigParams {
    val MinAppVersion = "Min App Version"
    val ContentUploadDir = "Content Upload Dir"
    val UploadedContentBaseURL = "Uploaded Content Base URL"
  }

  protected val defaultSectionName = "Web Service"
  protected def defaultConfiguration = Map(defaultSectionName -> ConfigSection(
    defaultSectionName,
    Map(
      ConfigParams.MinAppVersion -> "1",
      ConfigParams.ContentUploadDir -> "d:/tmp/",
      ConfigParams.UploadedContentBaseURL -> "http://static-1.questmeapp.com/"
    )))
}

