package controllers.domain.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

trait ApiConfigHolder extends ConfigHolder { this: APIAccessor =>

  val configSectionName = "API"
    
  object ConfigParams {
    val ProposalNormalDaysToEnterRotation = "Proposal Normal Days To Enter Rotation" 
    val ProposalAverageToWorstLikesRatio = "Proposal Average To Worst Likes Ratio"
    val ProposalCheatingRatio = "Proposal Cheating to Votes Ratio"
    val ProposalIACRatio = "Proposal IAC to Votes Ratio"
    val ProposalLikesToEnterRotation = "Proposal Likes To Enter Rotation (calculated)" 
    val ProposalVotesToLeaveVoting = "Proposal Votes To Leave Voting (calculated)" 
  }
  
  val defaultConfiguration = ConfigSection(
    configSectionName,
    Map(
        ConfigParams.ProposalNormalDaysToEnterRotation -> "7", 
        ConfigParams.ProposalAverageToWorstLikesRatio -> "1",
        ConfigParams.ProposalCheatingRatio -> "0.1",
        ConfigParams.ProposalIACRatio -> "0.03",
        ConfigParams.ProposalLikesToEnterRotation -> "10",
        ConfigParams.ProposalVotesToLeaveVoting -> "100"
        ))
}
