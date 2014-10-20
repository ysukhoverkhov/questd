package logic

import models.domain._

// TODO: check clean up here all constants (most of them are not used now).
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
   * Quest proposing.
   */
  val QuestProposalGiveUpPenalty = 2

  val QuestProposalCheatingPenalty = 10

  val QuestProposalIACPenalty = 10

  val MinQuestLevel = 2

  val MaxQuestLevel = 21

  val EasyWeight = 0
  val NormalWeight = 8
  val HardWeight = 15
  val ExtremeWeight = 22

  /**
   * Quest resolving.
   */

  val QuestForSolveLevelToleranceUp = 1
  val QuestForSolveLevelToleranceDown = 20

  val QuestSolutionGiveUpPenalty = 2

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

  val SolutionLevelDownTolerance = 30

  val SolutionLevelUpTolerance = 2

  val FriendsVoteMult = 2

  val InvitedVoteMult = 2

  val NumberOfFavoriteThemesForVIPSolutions = 3
  val NumberOfFavoriteThemesForOtherSolutions = 1


  /**
   * Messages
   */
  val NumberOfStoredMessages = 100


  // Constants bellow are used in algorithms and should not be changed.

  /**
   * Number of themes skips for coins.
   */
  def NumberOfThemesSkipsForCoins = 12

  /**
   * Number of quests skip for coins.
   */
  val NumberOfQuestsSkipsForCoins = 8

  /**
   * Friends
   */
  val NumberOfFreindsOnLastLevel = 100


  /**
   * Tasks
   */
  val RatingForCompletingDailyTasks = 500

  /**
   * Tutorial
   */
  val NumberOfStoredTutorialPlatforms = 10
  val MaxLengthOfTutorialPlatformState = 256
}

