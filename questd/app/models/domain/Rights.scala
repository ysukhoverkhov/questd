package models.domain

import play.api.libs.json.{Format, JsString, JsSuccess, JsValue}


object Functionality extends Enumeration {

  type Functionality = Value

  val VoteQuestSolutions = Value
  val SubmitPhotoSolutions = Value
  val SubmitVideoSolutions = Value
  val Report = Value
  val InviteFriends = Value
  val AddToFollowing = Value
  val VoteQuests = Value
  val SubmitPhotoQuests = Value
  val SubmitVideoQuests = Value
  val VoteReviews = Value
  val SubmitReviewsForSolutions = Value
  val SubmitReviewsForQuests = Value
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
    SubmitPhotoSolutions,
    SubmitVideoSolutions,
    Report,
    InviteFriends,
    AddToFollowing,
    VoteQuests,
    SubmitPhotoQuests,
    SubmitVideoQuests,
    VoteReviews,
    SubmitReviewsForSolutions,
    SubmitReviewsForQuests,
    GiveRewards))

  val none: Rights = Rights(Set())
}

