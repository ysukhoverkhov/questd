package models.domain

import play.api.libs.json.{Format, JsString, JsSuccess, JsValue}


object Functionality extends Enumeration {

  type Functionality = Value

  val VoteQuestSolutions = Value
  val SubmitPhotoResults = Value
  val SubmitVideoResults = Value
  val Report = Value
  val InviteFriends = Value
  val AddToFollowing = Value
  val VoteQuests = Value
  val SubmitPhotoQuests = Value
  val SubmitVideoQuests = Value
  val VoteReviews = Value
  val SubmitReviewsForResults = Value
  val SubmitReviewsForProposals = Value
  val GiveRewards = Value

  implicit val myEnumFormat = new Format[Functionality] {
    def reads(json: JsValue) = JsSuccess(Functionality.withName(json.as[String]))
    def writes(myEnum: Functionality) = JsString(myEnum.toString)
  }
}

/**
 * What does user can do an what level.
 */
case class Rights(
  unlockedFunctionality: Set[Functionality.Value] = Set(),
  maxFriendsCount: Int = 0)


object Rights {
  import models.domain.Functionality._

  /**
   * All rights given.
   */
  val full: Rights = Rights(Set(
    VoteQuestSolutions,
    SubmitPhotoResults,
    SubmitVideoResults,
    Report,
    InviteFriends,
    AddToFollowing,
    VoteQuests,
    SubmitPhotoQuests,
    SubmitVideoQuests,
    VoteReviews,
    SubmitReviewsForResults,
    SubmitReviewsForProposals,
    GiveRewards))

  val none: Rights = Rights(Set())
}

