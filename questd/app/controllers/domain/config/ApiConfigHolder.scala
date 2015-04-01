package controllers.domain.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

class _ConfigParams {

  val DebugExpMultiplier = "01 01 Debug Exp Multiplier"
  val DebugDisableProposalCoolDown = "01 02 Debug Disable Proposal CoolDown"

  val ActiveUserDays = "10 01 Active User Days"

  val SolutionVoteTaskCountMean = "30 01 Solution Vote Task Count Mean"
  val SolutionVoteTaskCountDeviation = "30 02 Solution Vote Task Count Deviation"
  val CreateSolutionTaskProbability = "30 11 Create Solution Task Probability"
  val AddToFollowingTaskProbability = "30 21 Add To Following Task Probability"
  val QuestVoteTaskCountMean = "30 31 Quest Vote Task Count Mean"
  val QuestVoteTaskCountDeviation = "30 32 Quest Vote Task Count Deviation"
  val CreateQuestTaskProbability = "30 41 Create Quest Task Probability"

  val FavoriteThemesShare = "40 01 Favorite Themes Share"
  val FavoriteThemesProbability = "40 02 Favorite Themes Probability"

  val ProposalCheatingRatio = "50 12 Proposal Cheating to Votes Ratio"
  val ProposalMinCheatingVotes = "50 13 Proposal Min Cheating Votes"
  val ProposalIACRatio = "50 13 Proposal IAC to Total Votes Ratio"
  val ProposalMinIACVotes = "50 14 Proposal min IAC Votes"
  val ProposalMaxDescriptionLength = "50 30 Proposal Max Description Length"

  val SolutionCheatingRatio = "60 10 Solution Cheating to Votes Ratio"
  val SolutionMinCheatingVotes = "60 11 Solution minimum votes to thing it's a cheating"
  val SolutionIACRatio = "60 15 Solution IAC to Votes Ratio"
  val SolutionMinIACVotes = "60 16 Solution min IAC Votes"

  val QuestProbabilityLevelsToGiveStartingQuests = "71 01 Level to give starting quests"
  val QuestProbabilityStartingVIPQuests = "71 02 Probability of selecting VIP quests in initial stage"
  val QuestProbabilityStartingFriendQuests = "71 03 Probability of selecting friends quests in initial stage"
  val QuestProbabilityStartingFollowingQuests = "71 04 Probability of selecting following quests in initial stage"

  val QuestProbabilityFriends = "72 01 Probability of quests from friends"
  val QuestProbabilityFollowing = "72 02 Probability of quests from Following"
  val QuestProbabilityVIP = "72 04 Probability of quests from Vips"

  var TimeLineRandomQuestsDaily = "80 01 TimeLine Random Quests Daily"
  var TimeLineRandomSolutionsDaily = "80 02 TimeLine Random Solutions Daily"
  var TimeLineRandomBattlesDaily = "80 03 TimeLine Random Battles Daily"

  val SolutionProbabilityLevelsToGiveStartingSolutions = "91 01 Level to give starting Solutions"
  val SolutionProbabilityStartingVIPSolutions = "91 02 Probability of selecting VIP Solutions in initial stage"

  val SolutionProbabilityFriends = "92 01 Probability of Solutions from friends"
  val SolutionProbabilityFollowing = "92 02 Probability of Solutions from Following"
  val SolutionProbabilityLiked = "92 03 Probability of liked Solutions"
  val SolutionProbabilityVIP = "92 04 Probability of quests from Vips"

  val BattleProbabilityLevelsToGiveStartingBattles = "95 01 Level to give starting Battles"
  val BattleProbabilityStartingVIPBattles = "95 02 Probability of selecting VIP Battles in initial stage"

  val BattleProbabilityFriends = "96 01 Probability of Battles from friends"
  val BattleProbabilityFollowing = "96 02 Probability of Battles from Following"
  val BattleProbabilityLikedSolutions = "96 03 Probability of Battles for liked Solutions"
  val BattleProbabilityVIP = "96 04 Probability of quests from VIPs"
}

object _ConfigParams extends _ConfigParams

trait ApiConfigHolder extends ConfigHolder { this: APIAccessor =>

  val configSectionName = "API"

  def ConfigParams = _ConfigParams

  val defaultConfiguration = ConfigSection(
    configSectionName,
    Map(
      ConfigParams.DebugExpMultiplier -> "1",
      ConfigParams.DebugDisableProposalCoolDown -> "0",

      ConfigParams.ActiveUserDays -> "7",

      ConfigParams.SolutionVoteTaskCountMean -> "3",
      ConfigParams.SolutionVoteTaskCountDeviation -> "1",
      ConfigParams.CreateSolutionTaskProbability -> "0.5",
      ConfigParams.AddToFollowingTaskProbability -> "0.3",
      ConfigParams.QuestVoteTaskCountMean -> "2",
      ConfigParams.QuestVoteTaskCountDeviation -> "0.5",
      ConfigParams.CreateQuestTaskProbability -> "0.3",

      ConfigParams.FavoriteThemesShare -> "0.2",
      ConfigParams.FavoriteThemesProbability -> "0.75",

      ConfigParams.ProposalCheatingRatio -> "0.1",
      ConfigParams.ProposalMinCheatingVotes -> "10",
      ConfigParams.ProposalIACRatio -> "0.03",
      ConfigParams.ProposalMinIACVotes -> "10",
      ConfigParams.ProposalMaxDescriptionLength -> "140",

      ConfigParams.SolutionCheatingRatio -> "0.1",
      ConfigParams.SolutionMinCheatingVotes -> "5",
      ConfigParams.SolutionIACRatio -> "0.03",
      ConfigParams.SolutionMinIACVotes -> "5",

      ConfigParams.QuestProbabilityLevelsToGiveStartingQuests -> "5",
      ConfigParams.QuestProbabilityStartingVIPQuests -> "0.5",
      ConfigParams.QuestProbabilityStartingFriendQuests -> "0.2",
      ConfigParams.QuestProbabilityStartingFollowingQuests -> "0.2",

      ConfigParams.QuestProbabilityFriends -> "0.40",
      ConfigParams.QuestProbabilityFollowing -> "0.30",
      ConfigParams.QuestProbabilityVIP -> "0.10",

      ConfigParams.TimeLineRandomQuestsDaily -> "5",
      ConfigParams.TimeLineRandomSolutionsDaily -> "5",
      ConfigParams.TimeLineRandomBattlesDaily -> "5",

      ConfigParams.SolutionProbabilityLevelsToGiveStartingSolutions -> "5",
      ConfigParams.SolutionProbabilityStartingVIPSolutions -> "0.5",

      ConfigParams.SolutionProbabilityFriends -> "0.25",
      ConfigParams.SolutionProbabilityFollowing -> "0.25",
      ConfigParams.SolutionProbabilityLiked -> "0.20",
      ConfigParams.SolutionProbabilityVIP -> "0.10",

      ConfigParams.BattleProbabilityLevelsToGiveStartingBattles -> "5",
      ConfigParams.BattleProbabilityStartingVIPBattles -> "0.5",

      ConfigParams.BattleProbabilityFriends -> "0.25",
      ConfigParams.BattleProbabilityFollowing -> "0.25",
      ConfigParams.BattleProbabilityLikedSolutions -> "0.20",
      ConfigParams.BattleProbabilityVIP -> "0.10"
    ))
}

