package controllers.web.rest

import play.api.libs.json._
import play.Logger

import controllers.domain.user._
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
    val InvalidFBToken = Value(1, "1")

    /**
     *  Supplied session is not valid on our server.
     */
    val SessionNotFound = Value(2, "2")
  }


  /**
   * Login Request
   * Single entry. Key - "token", value - value.
   */
  type WSLoginFBRequest = Map[String, String]

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

  
  /*********************
   * Solving quests
   *********************/
  type WSGetQuestCostResult = GetQuestCostResult
  
  type WSPurchaseQuestResult = PurchaseQuestResult
  
  type WSGetTakeQuestCostResult = GetTakeQuestCostResult
  
  type WSTakeQuestResult = TakeQuestResult

  type WSProposeSolutionRequest = ProposeSolutionRequest
  type WSProposeSolutionResult = ProposeSolutionResult
  
  type WSGetQuestGiveUpCostResult = GetQuestGiveUpCostResult
  
  type WSGiveUpQuestResult = GiveUpQuestResult

  
  /**********************
   * Voting quests
   **********************/
  type WSGetQuestToVoteResult = GetQuestToVoteResult
  
  type WSVoteQuestResult = VoteQuestResult
  
}
