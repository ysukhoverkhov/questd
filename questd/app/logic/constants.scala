package logic

import models.domain._

object constants {

  val MaxLevel = 20

  // user level restriction constants.
  val restrictions: Map[Functionality.Value, Int] = Map(
    Functionality.VoteQuestSolutions -> 1,
    Functionality.AddToFollowing -> 2,
    Functionality.SubmitPhotoResults -> 3,
    Functionality.SubmitVideoResults -> 4,
    Functionality.Report -> 5,
    Functionality.InviteFriends -> 6,
    Functionality.SubmitPhotoQuests -> 7,
    Functionality.SubmitVideoQuests -> 8,
    Functionality.VoteQuests -> 10,
    Functionality.VoteReviews -> 14,
    Functionality.SubmitReviewsForResults -> 16,
    Functionality.SubmitReviewsForProposals -> 18,
    Functionality.GiveRewards -> 20)

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
   */
  val QuestProposalCheatingPenalty = 10

  val QuestProposalIACPenalty = 10


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
  val NumberOfFriendsOnLastLevel = 100


  /**
   * Tasks
   */
  val RatingForCompletingDailyTasks = 250

}

