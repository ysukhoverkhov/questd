package controllers.domain.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

trait ApiConfigHolder extends ConfigHolder { this: APIAccessor =>

  val configSectionName = "API"
    
  object ConfigParams {
    val ProposalNormalDaysToEnterRotation = "Proposal Normal Days To Enter Rotation" 
    val ProposalDaysToLeaveVoting = "Proposal Days To Leave Voting" 
    val ProposalLikesToEnterRotation = "Proposal Likes To Enter Rotation (calculated)" 
  }
  
  val defaultConfiguration = ConfigSection(
    configSectionName,
    Map(
        ConfigParams.ProposalNormalDaysToEnterRotation -> "7", 
        ConfigParams.ProposalDaysToLeaveVoting -> "14",
        ConfigParams.ProposalLikesToEnterRotation -> "10"))
}
