package controllers.web.rest.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

trait WSConfigHolder extends ConfigHolder { this: APIAccessor =>
    val configSectionName = "Web Service"
    val defaultConfiguration = ConfigSection(
      configSectionName,
      Map(("Min App Version", "1")))
}

// TODO: add config initialization to config holder.
// TODO: initialize api config on each start of api.