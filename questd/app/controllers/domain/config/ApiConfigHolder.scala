package controllers.domain.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

class _ConfigParams {
  val ActiveUserDays = "10 01 Active User Days"

  val UserHistoryDays = "20 01 User History Days"

  val FavoriteThemesShare = "40 01 Favorite Themes Share"
  val FavoriteThemesProbability = "40 02 Favorite Themes Probability"

  val ProposalNormalDaysToEnterRotation = "50 01 Proposal Normal Days To Enter Rotation"
  val ProposalWorstLikesRatio = "50 02 Proposal Worst Likes Ratio"
  val ProposalCheatingRatio = "50 03 Proposal Cheating to Votes Ratio"
  val ProposalIACRatio = "50 04 Proposal IAC to Votes Ratio"
  val ProposalLikesToEnterRotation = "50 05 Proposal Likes To Enter Rotation (calculated)"
  val ProposalVotesToLeaveVoting = "50 06 Proposal Votes To Leave Voting (calculated)"
  val ProposalRatioToLeaveVoting = "50 07 Proposal Ratio To Leave Voting (calculated)"

  val QuestProbabilityLevelsToGiveStartingQuests = "71 01 Level to give starting quests"
  val QuestProbabilityStartingVIPQuests = "71 02 Probability of selecting VIP quests in initial stage"

  val QuestProbabilityFriends = "72 01 Probability of quests from friends"
  val QuestProbabilityShortlist = "72 02 Probability of quests from shortlist"
  val QuestProbabilityLiked = "72 03 Probability of liked quests"
  val QuestProbabilityStar = "72 04 Probability of quests from stars"
}

object _ConfigParams extends _ConfigParams

trait ApiConfigHolder extends ConfigHolder { this: APIAccessor =>

  val configSectionName = "API"

  def ConfigParams = _ConfigParams
  val defaultConfiguration = ConfigSection(
    configSectionName,
    Map(
      ConfigParams.ActiveUserDays -> "7",
      ConfigParams.UserHistoryDays -> "15",

      ConfigParams.FavoriteThemesShare -> "0.2",
      ConfigParams.FavoriteThemesProbability -> "0.75",

      ConfigParams.ProposalNormalDaysToEnterRotation -> "7",
      ConfigParams.ProposalWorstLikesRatio -> "0.1",
      ConfigParams.ProposalCheatingRatio -> "0.1",
      ConfigParams.ProposalIACRatio -> "0.03",
      ConfigParams.ProposalLikesToEnterRotation -> "10",
      ConfigParams.ProposalVotesToLeaveVoting -> "100",
      ConfigParams.ProposalRatioToLeaveVoting -> "0.05",

      ConfigParams.QuestProbabilityLevelsToGiveStartingQuests -> "5",
      ConfigParams.QuestProbabilityStartingVIPQuests -> "0.5",

      ConfigParams.QuestProbabilityFriends -> "0.25",
      ConfigParams.QuestProbabilityShortlist -> "0.25",
      ConfigParams.QuestProbabilityLiked -> "0.20",
      ConfigParams.QuestProbabilityStar -> "0.10"))
}

