package logic

object constants {

  val maxLevel = 20
  
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
  
  val minQuestLevel = 0
  
  val maxQuestLevel = 21
  
  
  /**
   * Quest resolving.
   */
  
  val questLevelTolerance = 20
  
  val questSolutionGiveUpPenalty = 2

  /**
   * Multiplier is relative to base (average) rating for resolving quests per day.
   */
  val questLosingMultiplier = 0.6666667
  
  /**
   * Multiplier is relative to quest losing
   */
  val questVictoryMultiplier = 2
  
  /**
   * Voting quest solutions.
   */
  
  val solutionLevelDownTolerance = 20
  
  val solutionLevelUpTolerance = 2
  
  
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

