package controllers.domain.config

import components.ConfigHolder
import components.APIAccessor
import models.domain.admin.ConfigSection

class _DefaultConfigParams {

  // Misc
  val DebugExpMultiplier = "01 01 Debug Exp Multiplier"
  val DebugDisableQuestCreationCoolDown = "01 02 Debug Disable Quest Creation CoolDown"

  val ActiveUserDays = "10 01 Active User Days"
  val DefaultCultureId = "15 01 Default Culture Id"

  val RequestsAutoRejectDays = "16 01 Default Culture Id"

  // Chat
  val ChatMaxMessageLength = "20 01 Chat Max Message Length"

  // Tasks
  val SolutionVoteTaskCountMean = "30 01 Solution Vote Task Count Mean"
  val SolutionVoteTaskCountDeviation = "30 02 Solution Vote Task Count Deviation"
  val CreateSolutionTaskProbability = "30 11 Create Solution Task Probability"
  val AddToFollowingTaskProbability = "30 21 Add To Following Task Probability"
  val QuestVoteTaskCountMean = "30 31 Quest Vote Task Count Mean"
  val QuestVoteTaskCountDeviation = "30 32 Quest Vote Task Count Deviation"
  val CreateQuestTaskProbability = "30 41 Create Quest Task Probability"
  val WriteCommentTaskProbability = "30 42 Write Comment Task Probability"
  val ChallengeBattleTaskProbability = "30 43 Challenge Battle Task Probability"
  val BattleVoteTaskProbability = "30 51 Battle Vote Task Probability"

  val FavoriteThemesShare = "40 01 Favorite Themes Share"
  val FavoriteThemesProbability = "40 02 Favorite Themes Probability"

  // Quests
  val CommentsMaxLength = "45 01 Comments Max Length"

  val QuestCheatingRatio = "50 12 Quest Cheating to Votes Ratio"
  val QuestMinCheatingVotes = "50 13 Quest Min Cheating Votes"
  val QuestIACRatio = "50 13 Quest IAC to Total Votes Ratio"
  val QuestMinIACVotes = "50 14 Quest min IAC Votes"
  val QuestMaxDescriptionLength = "50 30 Quest Max Description Length"
  val QuestMaxTimeLinePointsForSolve = "50 40 Quest Max Time Line Points For Solve"

  // Solutions
  val SolutionCheatingRatio = "60 10 Solution Cheating to Votes Ratio"
  val SolutionMinCheatingVotes = "60 11 Solution minimum votes to thing it's a cheating"
  val SolutionIACRatio = "60 15 Solution IAC to Votes Ratio"
  val SolutionMinIACVotes = "60 16 Solution min IAC Votes"
  val BattleCreationDelay = "60 21 Battle Creation Delay"

  // Battles
  val BattleMinVotesCount = "65 10 Battle Min Votes Count"
  val BattleAdditionalVotesMean = "65 11 Battle Additional Votes Mean"
  val BattleAdditionalVotesDeviation = "65 12 Battle Additional Votes Deviation"

  // Timeline
  val QuestProbabilityLevelsToGiveTutorialQuests = "71 00 Level to give tutorial quests"
  val QuestProbabilityLevelsToGiveStartingQuests = "71 01 Level to give starting quests"
  val QuestProbabilityStartingVIPQuests = "71 02 Probability of selecting VIP quests in initial stage"
  val QuestProbabilityStartingFriendQuests = "71 03 Probability of selecting friend's quests in initial stage"
  val QuestProbabilityStartingFollowingQuests = "71 04 Probability of selecting following quests in initial stage"

  val QuestProbabilityFriends = "72 01 Probability of quests from friends"
  val QuestProbabilityFollowing = "72 02 Probability of quests from Following"
  val QuestProbabilityVIP = "72 04 Probability of quests from VIPs"

  val SolutionProbabilityLevelsToGiveStartingSolutions = "91 01 Level to give starting Solutions"
  val SolutionProbabilityStartingVIPSolutions = "91 02 Probability of selecting VIP Solutions in initial stage"
  val SolutionProbabilityStartingFriendSolutions = "91 03 Probability of selecting Friend's Solutions in initial stage"
  val SolutionProbabilityStartingFollowingSolutions = "91 04 Probability of selecting Following Solutions in initial stage"

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


  var TimeLineRandomQuestsDailyMean = "80 01 TimeLine Random Quests Daily Mean"
  var TimeLineRandomQuestsDailyMeanFirstTime = "80 02 TimeLine Random Quests Daily Mean First Time"
  var TimeLineRandomQuestsDailyDeviation = "80 03 TimeLine Random Quests Daily Deviation"
  var TimeLineRandomQuestsDailyMin = "80 04 TimeLine Random Quests Daily Min"

  var TimeLineRandomSolutionsDailyMean = "80 11 TimeLine Random Solutions Daily Mean"
  var TimeLineRandomSolutionsDailyMeanFirstTime = "80 12 TimeLine Random Solutions Daily Mean First Time"
  var TimeLineRandomSolutionsDailyDeviation = "80 13 TimeLine Random Solutions Daily Deviation"
  var TimeLineRandomSolutionsDailyMin = "80 14 TimeLine Random Solutions Daily Min"

  var TimeLineRandomBattlesDailyMean = "80 21 TimeLine Random Battles Daily Mean"
  var TimeLineRandomBattlesDailyMeanFirstTime = "80 22 TimeLine Random Battles Daily Mean First Time"
  var TimeLineRandomBattlesDailyDeviation = "80 23 TimeLine Random Battles Daily Deviation"
  var TimeLineRandomBattlesDailyMin = "80 24 TimeLine Random Battles Daily Min"
}

class _TutorialConfigParams {
}

object _DefaultConfigParams extends _DefaultConfigParams
object _TutorialConfigParams extends _TutorialConfigParams

trait ApiConfigHolder extends ConfigHolder { this: APIAccessor =>

  def DefaultConfigParams = _DefaultConfigParams
  def TutorialConfigParams = _TutorialConfigParams

  protected override val defaultSectionName = "API"

  protected override def defaultConfiguration = Map(
    defaultSectionName -> ConfigSection(
          defaultSectionName,
          Map(
            DefaultConfigParams.DebugExpMultiplier -> "1",
            DefaultConfigParams.DebugDisableQuestCreationCoolDown -> "0",

            DefaultConfigParams.ActiveUserDays -> "7",
            DefaultConfigParams.DefaultCultureId -> "68349b7a-20ee-4f6e-8406-f468b30be783",

            DefaultConfigParams.RequestsAutoRejectDays -> "7",

            DefaultConfigParams.ChatMaxMessageLength -> "1024",

            DefaultConfigParams.SolutionVoteTaskCountMean -> "1",
            DefaultConfigParams.SolutionVoteTaskCountDeviation -> "0.5",
            DefaultConfigParams.CreateSolutionTaskProbability -> "0.5",
            DefaultConfigParams.AddToFollowingTaskProbability -> "0.3",
            DefaultConfigParams.QuestVoteTaskCountMean -> "2",
            DefaultConfigParams.QuestVoteTaskCountDeviation -> "0.5",
            DefaultConfigParams.CreateQuestTaskProbability -> "0.2",
            DefaultConfigParams.WriteCommentTaskProbability -> "0.3",
            DefaultConfigParams.ChallengeBattleTaskProbability -> "0.2",
            DefaultConfigParams.BattleVoteTaskProbability -> "0.3",


            DefaultConfigParams.FavoriteThemesShare -> "0.2",
            DefaultConfigParams.FavoriteThemesProbability -> "0.75",

            DefaultConfigParams.CommentsMaxLength -> "420",

            DefaultConfigParams.QuestCheatingRatio -> "0.1",
            DefaultConfigParams.QuestMinCheatingVotes -> "10",
            DefaultConfigParams.QuestIACRatio -> "0.03",
            DefaultConfigParams.QuestMinIACVotes -> "10",
            DefaultConfigParams.QuestMaxDescriptionLength -> "140",
            DefaultConfigParams.QuestMaxTimeLinePointsForSolve -> "20",

            DefaultConfigParams.SolutionCheatingRatio -> "0.1",
            DefaultConfigParams.SolutionMinCheatingVotes -> "5",
            DefaultConfigParams.SolutionIACRatio -> "0.03",
            DefaultConfigParams.SolutionMinIACVotes -> "5",
            DefaultConfigParams.BattleCreationDelay -> "24",

            DefaultConfigParams.BattleMinVotesCount -> "0",
            DefaultConfigParams.BattleAdditionalVotesMean -> "2",
            DefaultConfigParams.BattleAdditionalVotesDeviation -> "2",

            DefaultConfigParams.QuestProbabilityLevelsToGiveTutorialQuests -> "5",
            DefaultConfigParams.QuestProbabilityLevelsToGiveStartingQuests -> "7",
            DefaultConfigParams.QuestProbabilityStartingVIPQuests -> "0.5",
            DefaultConfigParams.QuestProbabilityStartingFriendQuests -> "0.2",
            DefaultConfigParams.QuestProbabilityStartingFollowingQuests -> "0.2",

            DefaultConfigParams.QuestProbabilityFriends -> "0.40",
            DefaultConfigParams.QuestProbabilityFollowing -> "0.30",
            DefaultConfigParams.QuestProbabilityVIP -> "0.10",

            DefaultConfigParams.TimeLineRandomQuestsDailyMean -> "5",
            DefaultConfigParams.TimeLineRandomQuestsDailyMeanFirstTime -> "15",
            DefaultConfigParams.TimeLineRandomQuestsDailyDeviation -> "1",
            DefaultConfigParams.TimeLineRandomQuestsDailyMin -> "4",

            DefaultConfigParams.TimeLineRandomSolutionsDailyMean -> "5",
            DefaultConfigParams.TimeLineRandomSolutionsDailyMeanFirstTime -> "15",
            DefaultConfigParams.TimeLineRandomSolutionsDailyDeviation -> "1",
            DefaultConfigParams.TimeLineRandomSolutionsDailyMin -> "4",

            DefaultConfigParams.TimeLineRandomBattlesDailyMean -> "3",
            DefaultConfigParams.TimeLineRandomBattlesDailyMeanFirstTime -> "0",
            DefaultConfigParams.TimeLineRandomBattlesDailyDeviation -> "1",
            DefaultConfigParams.TimeLineRandomBattlesDailyMin -> "1",

            DefaultConfigParams.SolutionProbabilityLevelsToGiveStartingSolutions -> "5",
            DefaultConfigParams.SolutionProbabilityStartingVIPSolutions -> "0.5",
            DefaultConfigParams.SolutionProbabilityStartingFriendSolutions -> "0.2",
            DefaultConfigParams.SolutionProbabilityStartingFollowingSolutions -> "0.2",

            DefaultConfigParams.SolutionProbabilityFriends -> "0.25",
            DefaultConfigParams.SolutionProbabilityFollowing -> "0.25",
            DefaultConfigParams.SolutionProbabilityLiked -> "0.20",
            DefaultConfigParams.SolutionProbabilityVIP -> "0.10",

            DefaultConfigParams.BattleProbabilityLevelsToGiveStartingBattles -> "5",
            DefaultConfigParams.BattleProbabilityStartingVIPBattles -> "0.5",

            DefaultConfigParams.BattleProbabilityFriends -> "0.25",
            DefaultConfigParams.BattleProbabilityFollowing -> "0.25",
            DefaultConfigParams.BattleProbabilityLikedSolutions -> "0.20",
            DefaultConfigParams.BattleProbabilityVIP -> "0.10"
          )),
    "Tutorial" -> ConfigSection(
      "Tutorial",
      Map(
      ))
  )
}

