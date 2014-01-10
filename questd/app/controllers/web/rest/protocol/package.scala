package controllers.web.rest

import play.api.libs.json._
import play.Logger

import controllers.domain.app.user._
import models.domain.Profile

package object protocol {

  /**
   * Payload in case of 401 error.
   */
  case class WSUnauthorisedResult(code: UnauthorisedReason.Value)

  /**
   *  Reasons of Unauthorised results.
   */
  object UnauthorisedReason extends Enumeration {

    type UnauthorisedReason = UnauthorisedReason.Value

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
   * Single entry. Key - "token", value - value.
   */
  case class WSLoginFBRequest (token: String, appVersion: Int)

  /**
   * Login Result
   * Single entry. Key - "token", value - value.
   */
  case class WSLoginFBResult(sessionid: String)

  /**
   * Get profile response
   */
  type WSProfileResult = Profile

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
  type WSProposeQuestRequest = ProposeQuestRequest

  /**
   *
   */
  type WSProposeQuestResult = ProposeQuestResult

  /**
   *
   */
  type WSGiveUpQuestProposalResult = GiveUpQuestProposalResult

  /**
   *
   */
  type WSGetQuestProposalGiveUpCostResult = GetQuestProposalGiveUpCostResult

  /**
   * *******************
   * Solving quests
   * *******************
   */
  type WSGetQuestCostResult = GetQuestCostResult

  type WSPurchaseQuestResult = PurchaseQuestResult

  type WSGetTakeQuestCostResult = GetTakeQuestCostResult

  type WSTakeQuestResult = TakeQuestResult

  type WSProposeSolutionRequest = ProposeSolutionRequest
  type WSProposeSolutionResult = ProposeSolutionResult

  type WSGetQuestGiveUpCostResult = GetQuestGiveUpCostResult

  type WSGiveUpQuestResult = GiveUpQuestResult

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
    duration: Option[String],

    /**
     * @see QuestDifficulty
     */
    difficulty: Option[String])

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
}

