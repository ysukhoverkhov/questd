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
  
  /**
   * Quest resolving.
   */
  
  val questSolutionGiveUpPenalty = 2

  /**
   * Number of themes skips for coins.
   */
  def numberOfThemesSkipsForCoins = 4
  
  /**
   * Number of quests skip for coins.
   */
  val numberOfQuestsSkipsForCoins = 8
}

