package controllers.domain.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

trait ApiConfigHolder extends ConfigHolder { this: APIAccessor =>
    val configSectionName = "API"
    val defaultConfiguration = ConfigSection(
      configSectionName,
      Map(("Votes To Approve", "10")))
}
