package models.domain

import logic.constants

/**
 * What does user can do an what level.
 */
case class Rights(
  voteQuestSolutions: Int = constants.voteQuestSolutions,
  submitPhotoResults: Int = constants.submitPhotoResults,
  submitVideoResults: Int = constants.submitVideoResults,
  report: Int = constants.report,
  inviteFriends: Int = constants.inviteFriends,
  addToShortList: Int = constants.addToShortList,
  voteQuestProposals: Int = constants.voteQuestProposals,
  submitPhotoQuests: Int = constants.submitPhotoQuests,
  submitVideoQuests: Int = constants.submitVideoQuests,
  voteReviews: Int = constants.voteReviews,
  submitReviewsForResults: Int = constants.submitReviewsForResults,
  submitReviewsForProposals: Int = constants.submitReviewsForProposals,
  giveRewards: Int = constants.giveRewards)
    
