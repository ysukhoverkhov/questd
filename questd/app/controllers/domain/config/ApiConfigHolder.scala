package controllers.domain.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

trait ApiConfigHolder extends ConfigHolder { this: APIAccessor =>
 
  object ConfigParams {
    val MaxDailyProposalVotes = "Max daily proposal votes" 
    val MaxDailyProposals = "Max daily proposals" 
  }
  
  val configSectionName = "API"
  val defaultConfiguration = ConfigSection(
    configSectionName,
    Map(
        ConfigParams.MaxDailyProposalVotes -> "10", 
        ConfigParams.MaxDailyProposals -> "10"))
}
