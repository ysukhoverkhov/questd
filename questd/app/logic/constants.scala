package logic

import models.domain._

object constants {

  val maxLevel = 20

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

  // user level restriction constants.
  val voteQuestSolutions: Int = 1
  val submitPhotoResults: Int = 3
  val submitVideoResults: Int = 4
  val report: Int = 5
  val inviteFriends: Int = 6
  val addToShortList: Int = 8
  val voteQuestProposals: Int = 10
  val submitPhotoQuests: Int = 12
  val submitVideoQuests: Int = 13
  val voteReviews: Int = 14
  val submitReviewsForResults: Int = 16
  val submitReviewsForProposals: Int = 18
  val giveRewards: Int = 20

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

  val questLevelToleranceUp = 1
  val questLevelToleranceDown = 20

  val questSolutionGiveUpPenalty = 2

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

  // Constants bellow are used in algorithms and should not be changed.

  /**
   * Number of themes skips for coins.
   */
  def numberOfThemesSkipsForCoins = 4

  /**
   * Number of quests skip for coins.
   */
  val numberOfQuestsSkipsForCoins = 8
}

