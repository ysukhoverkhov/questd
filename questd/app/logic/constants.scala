package logic

import models.domain.user.profile.Functionality

object constants {

  val MaxLevel = 20

  // user level restriction constants.
  val restrictions: Map[Functionality.Value, Int] = Map(
    Functionality.VoteQuests -> 1,
    Functionality.VoteSolutions -> 1,
    Functionality.VoteBattles -> 1,
    Functionality.VoteComments -> 1,
    Functionality.AddToFollowing -> 1,
    Functionality.SubmitPhotoSolutions -> 2,
    Functionality.ChallengeBattles -> 3,
    Functionality.Report -> 4,
    Functionality.PostComments -> 4,
    Functionality.InviteFriends -> 5,
    Functionality.AssignQuests -> 5,
    Functionality.SubmitPhotoQuests -> 6,
    Functionality.SubmitVideoSolutions -> 7,
    Functionality.SubmitVideoQuests -> 8,

    Functionality.GiveRewards -> 20)

  def levelFor(f: Functionality.Value): Int = {
    restrictions(f)
  }

  /**
   * Misc
   */

  val FlipHour = 5
  val DayStartHour = 12
  val DayEndHour = 21

  /**
   * Time line
   */

  val TimeLineContentLevelSigma = 2

  /**
   * Income
   */

  val PremiumIncomeMultiplier = 1.5

  val MaxRewardedQuestSolutionsPerDay = 2



  /**
   * Quest resolving.
   */
  // TODO: clean me up with tags.
  val NumberOfFavoriteThemesForVIPQuests = 3
  val NumberOfFavoriteThemesForOtherQuests = 1

  /**
   * Multiplier relative to daily xp income to quest solving.
   */
  val QuestSolvingMultiplier = 0.5

  /**
   * Multiplier is relative to base (average) rating for resolving quests per day.
   */
  val QuestLosingMultiplier = 1.0 / 3.0

  /**
   * Multiplier is relative to base (average) rating for resolving quests per day.
   */
  val QuestVictoryMultiplier = 2.0 / 3.0

  val QuestSolutionCheatingPenalty = 10

  val QuestSolutionIACPenalty = 10

  /**
   * Voting quest solutions.
   */

  val FriendsVoteMult = 2

  // TODO: clean me up with tags.
  val NumberOfFavoriteThemesForVIPSolutions = 3
  val NumberOfFavoriteThemesForOtherSolutions = 1


  /**
   * Messages
   */
  val NumberOfStoredMessages = 100


  // Constants bellow are used in algorithms and should not be changed.

  /**
   * Friends
   */
  val NumberOfFriendsOnLastLevel = 100


  /**
   * Tasks
   */
  val DailyTasksRatingForCompleting = 250
  val DailyTasksCoinsDeviation = 0.15

}

