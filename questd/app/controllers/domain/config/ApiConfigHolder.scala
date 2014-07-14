package controllers.domain.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

class _ConfigParams {

  val DebugExpMultiplier = "01 01 Debug Exp Multiplier"
  val DebugDisableProposalCooldown = "01 02 Debug Disable Proposal Cooldown"
  val DebugDisableSolutionCooldown = "01 03 Debug Disable Solution Cooldown"

  val ActiveUserDays = "10 01 Active User Days"

  val UserHistoryDays = "20 01 User History Days"

  val FavoriteThemesShare = "40 01 Favorite Themes Share"
  val FavoriteThemesProbability = "40 02 Favorite Themes Probability"

  val ProposalNormalDaysToEnterRotation = "50 01 Proposal Normal Days To Enter Rotation"
  val ProposalMinVotesToTakeRemovalDecision = "50 02 Min Votes To Take Removal Decision"
  val ProposalWorstLikesRatio = "50 11 Proposal Worst Likes Ratio" // If proposal has fewer than current ration multiplied by average votes/likes ratio and more than min votes count is reached it is removed from voting.
  val ProposalCheatingRatio = "50 12 Proposal Cheating to Votes Ratio"
  val ProposalIACRatio = "50 13 Proposal IAC to Total Votes Ratio"
  val ProposalMinIACVotes = "50 14 Proposal min IAC Votes"
  val ProposalLikesToEnterRotation = "50 21 Proposal Likes To Enter Rotation (calculated)"
  val ProposalVotesToLeaveVoting = "50 22 Proposal Votes To Leave Voting (calculated)"
  val ProposalRatioToLeaveVoting = "50 23 Proposal Ratio To Leave Voting (calculated)"

  val SolutionCheatingRatio = "60 10 Solution Cheating to Votes Ratio"
  val SolutionMinCheatingVotes = "60 11 Solution minimum votes to thing it's a cheating"
  val SolutionIACRatio = "60 15 Solution IAC to Votes Ratio"
  val SolutionMinIACVotes = "60 16 Solution min IAC Votes"
    
  val QuestProbabilityLevelsToGiveStartingQuests = "71 01 Level to give starting quests"
  val QuestProbabilityStartingVIPQuests = "71 02 Probability of selecting VIP quests in initial stage"

  val QuestProbabilityFriends = "72 01 Probability of quests from friends"
  val QuestProbabilityShortlist = "72 02 Probability of quests from shortlist"
  val QuestProbabilityLiked = "72 03 Probability of liked quests"
  val QuestProbabilityStar = "72 04 Probability of quests from stars"

  val SolutionProbabilityLevelsToGiveStartingSolutions = "91 01 Level to give starting Solutions"
  val SolutionProbabilityStartingVIPSolutions = "91 02 Probability of selecting VIP Solutions in initial stage"

  val SolutionProbabilityFriends = "92 01 Probability of Solutions from friends"
  val SolutionProbabilityShortlist = "92 02 Probability of Solutions from shortlist"
  val SolutionProbabilityLiked = "92 03 Probability of liked Solutions"
  val SolutionProbabilityStar = "92 04 Probability of quests from Solutions"

}

object _ConfigParams extends _ConfigParams

trait ApiConfigHolder extends ConfigHolder { this: APIAccessor =>

  val configSectionName = "API"

  def ConfigParams = _ConfigParams
  val defaultConfiguration = ConfigSection(
    configSectionName,
    Map(
      ConfigParams.DebugExpMultiplier -> "1",
      ConfigParams.DebugDisableProposalCooldown -> "0",
      ConfigParams.DebugDisableSolutionCooldown -> "0",

      ConfigParams.ActiveUserDays -> "7",
      ConfigParams.UserHistoryDays -> "15",

      ConfigParams.FavoriteThemesShare -> "0.2",
      ConfigParams.FavoriteThemesProbability -> "0.75",

      ConfigParams.ProposalNormalDaysToEnterRotation -> "7",
      ConfigParams.ProposalMinVotesToTakeRemovalDecision -> "10",

      ConfigParams.ProposalWorstLikesRatio -> "0.1",
      ConfigParams.ProposalCheatingRatio -> "0.1",
      ConfigParams.ProposalIACRatio -> "0.03",
      ConfigParams.ProposalMinIACVotes -> "2",
      ConfigParams.ProposalLikesToEnterRotation -> "10",
      ConfigParams.ProposalVotesToLeaveVoting -> "100",
      ConfigParams.ProposalRatioToLeaveVoting -> "0.05",

      ConfigParams.SolutionCheatingRatio -> "0.1",
      ConfigParams.SolutionMinCheatingVotes -> "5",
      ConfigParams.SolutionIACRatio -> "0.03",
      ConfigParams.SolutionMinIACVotes -> "2",
      
      ConfigParams.QuestProbabilityLevelsToGiveStartingQuests -> "5",
      ConfigParams.QuestProbabilityStartingVIPQuests -> "0.5",

      ConfigParams.QuestProbabilityFriends -> "0.25",
      ConfigParams.QuestProbabilityShortlist -> "0.25",
      ConfigParams.QuestProbabilityLiked -> "0.20",
      ConfigParams.QuestProbabilityStar -> "0.10",

      ConfigParams.SolutionProbabilityLevelsToGiveStartingSolutions -> "5",
      ConfigParams.SolutionProbabilityStartingVIPSolutions -> "0.5",

      ConfigParams.SolutionProbabilityFriends -> "0.25",
      ConfigParams.SolutionProbabilityShortlist -> "0.25",
      ConfigParams.SolutionProbabilityLiked -> "0.20",
      ConfigParams.SolutionProbabilityStar -> "0.10"))
}

