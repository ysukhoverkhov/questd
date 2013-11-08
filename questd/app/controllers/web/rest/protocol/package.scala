package controllers.web.rest

import play.api.libs.json._
import controllers.domain.user._
import play.Logger

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
   * Get Quest theme cost result
   */
  type WSGetQuestThemeCostResult = GetQuestThemeCostResult

}
