package models.domain

import logic.constants

/**
 * This can be given to client as is, thus contains only public information.
 */
case class Profile(
  level: Int = 1,
  bio: Bio = Bio(),
  assets: Assets = Assets(0, 0, 0),
  rights: Rights = Rights(),
  questProposalContext: QuestProposalConext = QuestProposalConext())

/**
 * What does user can do an what level.
 */
case class Rights(
  voteQuestResults: Int = constants.voteQuestResults,
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
    
