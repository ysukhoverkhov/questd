package models.domain.user

object Functionality extends Enumeration {

  type Functionality = Value

  val VoteQuests = Value
  val VoteSolutions = Value
  val VoteBattles = Value
  val SubmitPhotoSolutions = Value
  val SubmitVideoSolutions = Value
  val Report = Value
  val InviteFriends = Value
  val AddToFollowing = Value
  val SubmitPhotoQuests = Value
  val SubmitVideoQuests = Value
  val VoteReviews = Value
  val SubmitReviewsForSolutions = Value
  val SubmitReviewsForQuests = Value
  val GiveRewards = Value
}

/**
 * What does user can do an what level.
 */
case class Rights(
  unlockedFunctionality: Set[Functionality.Value] = Set(),
  maxFriendsCount: Int = 0)


object Rights {

  /**
   * All rights given.
   */
  val full: Rights = Rights(Set(
    Functionality.VoteQuests,
    Functionality.VoteSolutions,
    Functionality.VoteBattles,
    Functionality.SubmitPhotoSolutions,
    Functionality.SubmitVideoSolutions,
    Functionality.Report,
    Functionality.InviteFriends,
    Functionality.AddToFollowing,
    Functionality.SubmitPhotoQuests,
    Functionality.SubmitVideoQuests,
    Functionality.VoteReviews,
    Functionality.SubmitReviewsForSolutions,
    Functionality.SubmitReviewsForQuests,
    Functionality.GiveRewards))

  val none: Rights = Rights(Set())
}

