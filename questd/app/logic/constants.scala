package logic

import models.domain.user.Functionality

object constants {

  val MaxLevel = 20

  // user level restriction constants.
  val restrictions: Map[Functionality.Value, Int] = Map(
    Functionality.VoteQuestSolutions -> 1,
    Functionality.AddToFollowing -> 1,
    Functionality.SubmitPhotoSolutions -> 1,
    Functionality.SubmitVideoSolutions -> 1,
    Functionality.Report -> 1,
    Functionality.InviteFriends -> 1,
    Functionality.SubmitPhotoQuests -> 1,
    Functionality.SubmitVideoQuests -> 1,
    Functionality.VoteQuests -> 1, // TODO: tweak it.
    Functionality.VoteReviews -> 1,
    Functionality.SubmitReviewsForSolutions -> 1,
    Functionality.SubmitReviewsForQuests -> 1,
    Functionality.GiveRewards -> 1)

  def levelFor(f: Functionality.Value): Int = {
    restrictions(f)
  }

  /**
   * Misc
   */

  val FlipHour = 5

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
   * Quest proposing.
   */ // TODO: find out why this is not used and what is used instead of this.
  val QuestCheatingPenalty = 10

  val QuestIACPenalty = 10


  /**
   * Quest resolving.
   */
  // TODO: clean me up with tags.
  val NumberOfFavoriteThemesForVIPQuests = 3
  val NumberOfFavoriteThemesForOtherQuests = 1

  /**
   * Multiplier is relative to base (average) rating for resolving quests per day.
   */
  val QuestLosingMultiplier = 0.6666667

  /**
   * Multiplier is relative to quest losing
   */
  val QuestVictoryMultiplier = 2

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
  val NumberOfFriendsOnLastLevel = 10000


  /**
   * Tasks
   */
  val DailyTasksRatingForCompleting = 250
  val DailyTasksCoinsDeviation = 0.15

}

