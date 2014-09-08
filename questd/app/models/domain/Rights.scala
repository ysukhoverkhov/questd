package models.domain

import logic.constants

object Functionality extends Enumeration {
  val VoteQuestSolutions = Value
  val SubmitPhotoResults = Value
  val SubmitVideoResults = Value
  val Report = Value
  val InviteFriends = Value
  val AddToShortList = Value
  val VoteQuestProposals = Value
  val SubmitPhotoQuests = Value
  val SubmitVideoQuests = Value
  val VoteReviews = Value
  val SubmitReviewsForResults = Value
  val SubmitReviewsForProposals = Value
  val GiveRewards = Value
}

/**
 * What does user can do an what level.
 */
case class Rights(
  val unlockedFunctionality: Set[Functionality.Value] = Set(),
  val maxFriendsCount: Int = 0)


object Rights {
  import Functionality._

  /**
   * All rights given.
   */
  val full: Rights = Rights(Set(
    VoteQuestSolutions,
    SubmitPhotoResults,
    SubmitVideoResults,
    Report,
    InviteFriends,
    AddToShortList,
    VoteQuestProposals,
    SubmitPhotoQuests,
    SubmitVideoQuests,
    VoteReviews,
    SubmitReviewsForResults,
    SubmitReviewsForProposals,
    GiveRewards))
}

