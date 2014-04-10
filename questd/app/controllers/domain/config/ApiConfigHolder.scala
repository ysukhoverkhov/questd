package controllers.domain.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

trait ApiConfigHolder extends ConfigHolder { this: APIAccessor =>

  val configSectionName = "API"
    
  object ConfigParams {
    val ActiveUserDays = "10 01 Active User Days" 

    val UserHistoryDays = "20 01 User History Days" 

    val ProposalNormalDaysToEnterRotation = "50 01 Proposal Normal Days To Enter Rotation" 
    val ProposalWorstLikesRatio = "50 02 Proposal Worst Likes Ratio"
    val ProposalCheatingRatio = "50 03 Proposal Cheating to Votes Ratio"
    val ProposalIACRatio = "50 04 Proposal IAC to Votes Ratio"
    val ProposalLikesToEnterRotation = "50 05 Proposal Likes To Enter Rotation (calculated)" 
    val ProposalVotesToLeaveVoting = "50 06 Proposal Votes To Leave Voting (calculated)" 
    val ProposalRatioToLeaveVoting = "50 07 Proposal Ratio To Leave Voting (calculated)" 
  }
  
  val defaultConfiguration = ConfigSection(
    configSectionName,
    Map(
        ConfigParams.ActiveUserDays -> "7",
        ConfigParams.UserHistoryDays -> "15",
        
        ConfigParams.ProposalNormalDaysToEnterRotation -> "7", 
        ConfigParams.ProposalWorstLikesRatio -> "0.1",
        ConfigParams.ProposalCheatingRatio -> "0.1",
        ConfigParams.ProposalIACRatio -> "0.03",
        ConfigParams.ProposalLikesToEnterRotation -> "10",
        ConfigParams.ProposalVotesToLeaveVoting -> "100",
        ConfigParams.ProposalRatioToLeaveVoting -> "0.05"
        ))
}
