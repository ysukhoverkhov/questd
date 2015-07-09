package controllers.tasks.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

trait TasksConfigHolder extends ConfigHolder { this: APIAccessor =>
    protected val defaultSectionName = "Tasks"
    protected val defaultConfiguration = Map(defaultSectionName -> ConfigSection(
      defaultSectionName,
      Map(("akka://application/user/UsersHourlyCrawler", "0 0 0/1 * * ?"),
          ("akka://application/user/UsersWeeklyCrawler", "0 0 5 ? * MON"),
          ("akka://application/user/BattlesHourlyCrawler", "0 0 0/1 * * ?")
      )))
}
