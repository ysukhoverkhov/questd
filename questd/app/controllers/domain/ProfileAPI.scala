package controllers.domain

import models.domain.profile._
import models.domain.user._
import models.store.store._

import controllers.domain.base.base._

object ProfileAPI {

  /*
   * GetName
   */
  case class GetNameParams(sessionId: SessionID, userId: UserID) extends AuthorizedAPIRequestParams

  object GetNameResultCode extends Enumeration {
    val Ok = Value(1)
    val SessionNotFound = Value(2)
  }
  case class GetNameResult(result: GetNameResultCode.Value, username: String) 

  def getName(params: GetNameParams): ApiResult[GetNameResult] = {
    OkApiResult(Some(GetNameResult(GetNameResultCode.Ok, params.sessionId.toString)))
  }

}

