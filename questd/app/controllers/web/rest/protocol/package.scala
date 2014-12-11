package controllers.web.rest

import controllers.domain.app.user._
import models.domain._
import controllers.domain.app.misc.GetTimeResult

import scala.language.implicitConversions

package object protocol {

  /**
   * Payload in case of 401 error.
   */
  case class WSUnauthorisedResult(code: UnauthorisedReason.Value)

  /**
   *  Reasons of Unauthorised results.
   */
  object UnauthorisedReason extends Enumeration {

    /**
     *  FB tells us it doesn't know the token.
     */
    val InvalidFBToken = Value

    /**
     *  Supplied session is not valid on our server.
     */
    val SessionNotFound = Value

    /**
     * Passed version of the application is not supported.
     */
    val UnsupportedAppVersion = Value
  }

  /**
   * Login Request
   */
  case class WSLoginRequest(snName:String, token: String, appVersion: Int)

  /**
   * Login Result
   */
  case class WSLoginResult(sessionid: String)

  /**
   * Get profile response
   */
  type WSProfileResult = Profile

  /**
   * Set Gender protocol.
   */
  type WSSetGenderResult = SetGenderResult
  case class WSSetGenderRequest(gender: String)

  /**
   * Set debug protocol.
   */
  type WSSetDebugResult = SetDebugResult
  case class WSSetDebugRequest(debug: String)

  /**
   * Set city protocol.
   */
  type WSSetCityResult = SetCityResult
  case class WSSetCityRequest(city: String)

  /**
   * Set country protocol
   */
  type WSGetCountryListResult = GetCountryListResult
  type WSSetCountryResult = SetCountryResult
  case class WSSetCountryRequest(country: String)

  /**
   * Get Quest theme cost result
   */
  type WSGetQuestThemeCostResult = GetQuestThemeCostResult

  /**
   * Result for purchase quest.
   */
  type WSPurchaseQuestThemeResult = PurchaseQuestThemeResult

  /**
   * Take theme for inventing quest.
   */
  type WSTakeQuestThemeResult = TakeQuestThemeResult

  type WSGetQuestThemeTakeCostResult = GetQuestThemeTakeCostResult

  /**
   *
   */
  type WSProposeQuestResult = ProposeQuestResult

  case class WSContentReference(
    contentType: String,
    storage: String,
    reference: String) {

  }
  object WSContentReference {
    implicit def toContentReference(v: WSContentReference): ContentReference = {
      ContentReference(
        contentType = ContentType.withName(v.contentType),
        storage = v.storage,
        reference = v.reference
      )
    }
  }

  case class WSProposeQuestRequest(
    media: WSContentReference,
    icon: Option[WSContentReference] = None,
    description: String)

  /**
   *
   */
  type WSGiveUpQuestProposalResult = GiveUpQuestProposalResult

  /**
   *
   */
  type WSGetQuestProposalGiveUpCostResult = GetQuestProposalGiveUpCostResult

  type WSGetQuestProposalHelpCostResult = GetQuestProposalHelpCostResult

  /**
   * *******************
   * Solving quests
   * *******************
   */
  type WSGetQuestCostResult = GetQuestCostResult

  type WSPurchaseQuestResult = PurchaseQuestResult

  type WSGetTakeQuestCostResult = GetTakeQuestCostResult

  type WSTakeQuestResult = TakeQuestResult

  case class WSProposeSolutionRequest(
    media: WSContentReference,
    icon: Option[WSContentReference] = None)
  object WSProposeSolutionRequest {
    implicit def toSolutionInfoContent(v: WSProposeSolutionRequest): QuestSolutionInfoContent = {
      QuestSolutionInfoContent(
        media = v.media,
        icon = v.icon.map(r => r))
    }
  }


  type WSProposeSolutionResult = ProposeSolutionResult

  type WSGetQuestGiveUpCostResult = GetQuestGiveUpCostResult

  type WSGiveUpQuestResult = GiveUpQuestResult

  type WSGetQuestSolutionHelpCostResult = GetQuestSolutionHelpCostResult

  /**
   * ********************
   * Voting quest proposals
   * ********************
   */

  case class WSQuestProposalVoteRequest(
    /**
     * @see controllers.domain.user.QuestProposalVote
     */
    vote: String,

    /**
     * @see QuestDuration
     */
    duration: String,

    /**
     * @see QuestDifficulty
     */
    difficulty: String)

  type WSGetQuestProposalToVoteResult = GetQuestProposalToVoteResult

  type WSVoteQuestProposalResult = VoteQuestProposalResult

  /**
   * ********************
   * Voting quest solutions
   * ********************
   */

  case class WSQuestSolutionVoteRequest(
    /**
     * @see controllers.domain.user.QuestSolutionVote
     */
    vote: String)

  type WSGetQuestSolutionToVoteResult = GetQuestSolutionToVoteResult

  type WSVoteQuestSolutionResult = VoteQuestSolutionResult

  /**
   * ********************
   * Daily result
   * ********************
   */
  type WSGetDailyResultResult = GetDailyResultResult

  type WSShiftDailyResultResult = ShiftDailyResultResult

  case class WSGetRightsAtLevelsRequest(
    levelFrom: Int,
    levelTo: Int)

  type WSGetRightsAtLevelsResult = GetRightsAtLevelsResult

  type WSGetLevelsForRightsResult = GetLevelsForRightsResult

  case class WSGetLevelsForRightsRequest(
    functionality: List[String])

  /**
   * ********************
   * Content
   * ********************
   */
  case class WSGetQuestRequest(
    id: String)

  type WSGetQuestResult = GetQuestResult

  case class WSGetSolutionRequest(
    id: String)

  type WSGetSolutionResult = GetSolutionResult

  case class WSGetPublicProfilesRequest(
    ids: List[String])

  type WSGetPublicProfileResult = GetPublicProfilesResult

  case class WSGetSolutionsForQuestRequest(
    id: String,

    // see QuestSolutionStatus enum. if missing all solutions will be returned.
    status: List[String] = List(),

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int)

  type WSGetOwnSolutionsResult = GetOwnSolutionsResult
  case class WSGetOwnSolutionsRequest(
    // see QuestSolutionStatus enum. if missing all solutions will be returned.
    status: List[String] = List(),

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int)

  type WSGetOwnQuestsResult = GetOwnQuestsResult
  case class WSGetOwnQuestsRequest(
    // see QuestStatus enum. if missing all solutions will be returned.
    status: List[String] = List(),

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int)

  type WSGetSolutionsForQuestResult = GetSolutionsForQuestResult

  case class WSGetSolutionsForUserRequest(
    id: String,

    // see QuestSolutionStatus enum. if missing all solutions will be returned.
    status: List[String] = List(),

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int)

  type WSGetSolutionsForUserResult = GetSolutionsForUserResult

  case class WSGetQuestsForUserRequest(
    id: String,

    // see QuestSolutionStatus enum. if missing all solutions will be returned.
    status: List[String] = List(),

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int)

  type WSGetQuestsForUserResult = GetQuestsForUserResult

  /**
   * Shortlist
   */

  type WSGetShortlistResult = GetShortlistResult

  type WSCostToShortlistResult = CostToShortlistResult

  case class WSAddToShortlistRequest(
    /// Id of a person to add.
    id: String)

  type WSAddToShortlistResult = AddToShortlistResult

  case class WSRemoveFromShortlistRequest(
    /// Id of a person to remove.
    id: String)

  type WSRemoveFromShortlistResult = RemoveFromShortlistResult

  case class WSGetSuggestsForShortlistRequest(
    tokens: Map[String, String])

  type WSGetSuggestsForShortlistResult = GetSuggestsForShortlistResult

  /**
   * Friends
   */

  type WSGetFriendsResult = GetFriendsResult

  case class WSCostToRequestFriendshipRequest(
    /// Id of a person to add to friends.
    id: String)
  type WSCostToRequestFriendshipResult = CostToRequestFriendshipResult

  case class WSAskFriendshipRequest(
    /// Id of a person to add to friends.
    id: String)
  type WSAskFriendshipResult = AskFriendshipResult

  case class WSRespondFriendshipRequest(
    /// Id of a person we respond to.
    id: String,
    accepted: Boolean)
  type WSRespondFriendshipResult = RespondFriendshipResult

  case class WSRemoveFromFriendsRequest(
    /// Id of a person to add to friends.
    id: String)
  type WSRemoveFromFriendsResult = RemoveFromFriendsResult

  /**
   * Messages
   */

  type WSGetMessagesResult = GetMessagesResult

  case class WSRemoveMessageRequest(
    /// Id of a message to remove.
    id: String)
  type WSRemoveMessageResult = RemoveMessageResult

  /**
   * Misc
   */

  type WSGetTimeResult = GetTimeResult

  /**
   * Tutorial
   */

  case class WSGetTutorialStateRequest(
    /// Id of a platform to get state for.
    platformId: String)

  /// if no state for platform present empty result will be returned.
  type WSGetTutorialStateResult = GetTutorialStateResult

  case class WSSetTutorialStateRequest(
    /// Id of a platform to get state for.
    platformId: String,
    state: String)

  /// may return LimitExceeded in "allowed" field if there are too many platforms (logic.constants.NumberOfStoredTutorialPlatforms)
  /// or state is too long (logic.constants.MaxLengthOfTutorlaPlatformState).
  type WSSetTutorialStateResult = SetTutorialStateResult

  case class WSAssignTutorialTaskRequest(
    taskId: String)

  /// LimitExceeded if task was already requested.
  /// OutOfContent if task with this id is not exists.
  type WSAssignTutorialTaskResult = AssignTutorialTaskResult

  case class WSIncTutorialTaskRequest(
    taskId: String)

  /// OutOfContent if the task is not in active tasks.
  type WSIncTutorialTaskResult = IncTutorialTaskResult
}

