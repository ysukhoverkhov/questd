package logic

import models.domain._

object constants {

  val maxLevel = 20

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

  val flipHour = 5

  /**
   * Quest proposing.
   */
  val questProposalGiveUpPenalty = 2

  val questProposalCheatingPenalty = 10

  val questProposalIACPenalty = 10

  val minQuestLevel = 2

  val maxQuestLevel = 21

  val easyWeight = 0
  val normalWeight = 8
  val hardWeight = 15
  val extremeWeight = 22

  /**
   * Quest resolving.
   */

  val questForSolveLevelToleranceUp = 1
  val questForSolveLevelToleranceDown = 20

  val questSolutionGiveUpPenalty = 2

  val numberOfFavoriteThemesForVIPQuests = 3
  val numberOfFavoriteThemesForOtherQuests = 1

  /**
   * Multiplier is relative to base (average) rating for resolving quests per day.
   */
  val questLosingMultiplier = 0.6666667

  /**
   * Multiplier is relative to quest losing
   */
  val questVictoryMultiplier = 2

  val questSolutionCheatingPenalty = 10

  val questSolutionIACPenalty = 10

  /**
   * Voting quest solutions.
   */

  val solutionLevelDownTolerance = 30

  val solutionLevelUpTolerance = 2

  val friendsVoteMult = 2

  val invitedVoteMult = 2

  val numberOfFavoriteThemesForVIPSolutions = 3
  val numberOfFavoriteThemesForOtherSolutions = 1

  
  /**
   * Messages
   */
  val numberOfStoredMessages = 100
  
  
  // Constants bellow are used in algorithms and should not be changed.

  /**
   * Number of themes skips for coins.
   */
  def numberOfThemesSkipsForCoins = 4

  /**
   * Number of quests skip for coins.
   */
  val numberOfQuestsSkipsForCoins = 8

  /**
   * Friends
   */
  val numberOfFreindsOnLastLevel = 100
  
  
  /**
   * Tasks
   */
  val RatingForCompletingDailyTasks = 500
  
}

