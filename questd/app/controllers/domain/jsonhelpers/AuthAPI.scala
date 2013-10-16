package controllers.domain.jsonhelpers

import play.api.libs.json._
import play.api.libs.functional.syntax._
import controllers.domain.AuthAPI._

object AuthAPI {

  implicit val loginParamReads = (
    (__ \ 'name).read[String] and
    (__ \ 'pass).read[String])(LoginParams)

  implicit val loginResultWrites = (
    (__ \ "name").write[String] and
    (__ \ "pass").write[String])(unlift(LoginResult.unapply))

    
  implicit val registerParamReads = (
    (__ \ 'name).read[String] and
    (__ \ 'pass).read[String])(RegisterParams)

  implicit val registerResultWrites = (
    (__ \ "name").write[String] and
    (__ \ "pass").write[String])(unlift(RegisterResult.unapply))

}

