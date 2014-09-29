package controllers.tasks.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

trait TasksConfigHolder extends ConfigHolder { this: APIAccessor =>
    val configSectionName = "Tasks"
    val defaultConfiguration = ConfigSection(
      configSectionName,
      Map(("akka://application/user/UsersHourlyCrawler", "0 0 0/1 * * ?"),
          ("akka://application/user/UsersWeeklyCrawler", "0 0 5 ? * MON")))
}
