package controllers.web.rest.config

import components.{APIAccessor, ConfigHolder}
import models.domain.admin.ConfigSection

trait WSConfigHolder extends ConfigHolder { this: APIAccessor =>

  object ConfigParams {
    val ProtocolVersion = "Protocol Version"
    val ContentUploadDir = "Content Upload Dir"
    val UploadedContentBaseURL = "Uploaded Content Base URL"
  }

  protected val defaultSectionName = "Web Service"
  protected def defaultConfiguration = Map(defaultSectionName -> ConfigSection(
    defaultSectionName,
    Map(
      ConfigParams.ProtocolVersion -> "1",
      ConfigParams.ContentUploadDir -> "/var/www/vhosts/questmeapp.com/static-1.questmeapp.com/content_0.30",
      ConfigParams.UploadedContentBaseURL -> "http://static-1.questmeapp.com/content_0.30/"
    )))
}

