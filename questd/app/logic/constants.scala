package logic

import models.domain._

object constants {

  val MaxLevel = 20

  // user level restriction constants.
  val restrictions: Map[String, Int] = Map(
    Functionality.VoteQuestSolutions.toString() -> 1,
    Functionality.SubmitPhotoResults.toString() -> 3,
    Functionality.SubmitVideoResults.toString() -> 4,
    Functionality.Report.toString() -> 5,
    Functionality.InviteFriends.toString() -> 6,
    Functionality.AddToShortList.toString() -> 8,
    Functionality.VoteQuestProposals.toString() -> 10,
    Functionality.SubmitPhotoQuests.toString() -> 12,
    Functionality.SubmitVideoQuests.toString() -> 13,
    Functionality.VoteReviews.toString() -> 14,
    Functionality.SubmitReviewsForResults.toString() -> 16,
    Functionality.SubmitReviewsForProposals.toString() -> 18,
    Functionality.GiveRewards.toString() -> 20)

  def levelFor(f: Functionality.Value): Int = {
    restrictions(f.toString())
  }

  val FlipHour = 5

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
  def NumberOfThemesSkipsForCoins = 4

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
  
}

