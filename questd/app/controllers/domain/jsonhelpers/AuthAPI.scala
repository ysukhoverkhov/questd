package controllers.domain.jsonhelpers

import play.api.libs.json._
import play.api.libs.functional.syntax._
import controllers.domain.AuthAPI._

// http://www.playframework.com/documentation/2.1.2/ScalaJsonCombinators
object AuthAPI {

  /*
   * LoginParams
   */
  implicit val loginParamReads = (
    (__ \ 'name).read[String] and
    (__ \ 'pass).read[String])(LoginParams)

  /*
     * LoginResult
     */
  implicit val loginResultWrites = new Writes[LoginResult] {
    def writes(c: LoginResult): JsValue = {
      Json.obj(
        "result" -> c.result.id,
        "session" -> c.session.toString)
    }
  }

  //  implicit val loginResultWrites = (
  //    (__ \ "name").write[String] and
  //    (__ \ "pass").write[String])(unlift(LoginResult.unapply))

  /*
   * RegisterParams
   */
  implicit val registerParamReads = (
    (__ \ 'name).read[String] and
    (__ \ 'pass).read[String])(RegisterParams)

  /*
   * RegisterResult
   */
  implicit val registerResultWrites = new Writes[RegisterResult] {
    def writes(c: RegisterResult): JsValue = {
      Json.obj(
        "result" -> c.result.id)
    }
  }

}

