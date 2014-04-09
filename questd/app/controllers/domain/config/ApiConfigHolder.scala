package controllers.domain.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

trait ApiConfigHolder extends ConfigHolder { this: APIAccessor =>
 
  object ConfigParams {
    val TotalDailyProposalVotes = "Total daily proposal votes" 
    val TotalDailyProposalLikes = "Total daily proposal likes" 
    val ProposalsCountOnVoting = "Proposals count on voting" 
  }
  
  val configSectionName = "API"
  val defaultConfiguration = ConfigSection(
    configSectionName,
    Map(
        ConfigParams.TotalDailyProposalVotes -> "10", 
        ConfigParams.TotalDailyProposalLikes -> "5",
        ConfigParams.ProposalsCountOnVoting -> "10"))
}
