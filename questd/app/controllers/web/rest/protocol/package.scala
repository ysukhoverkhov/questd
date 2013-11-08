package controllers.web.rest

import play.api.libs.json._

package object protocol {

   
  /*
   * Payload in case of 401 error.
   */
  case class WSUnauthorisedResult(code: UnauthorisedReason.Value)

  // Reasons of Unauthorised results.
  object UnauthorisedReason extends Enumeration {
    
    // FB tells us it doesn't know the token.
    val InvalidFBToken = Value(1)
    
    // Supplied session is not valid on our server.
    val SessionNotFound = Value(2)
  }

  implicit val unathorisedWrites = new Writes[WSUnauthorisedResult] {
    def writes(c: WSUnauthorisedResult): JsValue = Json.obj("code" -> c.code.id)
  }

  
  /*
   * Login
   */
  // Single entry. Key - "token", value - value.
  type WSLoginFBRequest = Map[String, String]
  case class WSLoginFBResult(sessionid: String)

  implicit val loginFBResultWrites = new Writes[WSLoginFBResult] {
    def writes(c: WSLoginFBResult): JsValue = Json.obj("sessionid" -> c.sessionid)
  }
  
}