package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import play.Logger
import controllers.domain.app.protocol.ProfileModificationResult._
import java.util.Date

case class GetTimeRequest(user: User)
case class GetTimeResult(time: Date)

private[domain] trait MiscAPI { this: DBAccessor =>

  /**
   * Get server's time.
   * TODO move me out of user to misc api.
   */
  def getTime(request: GetTimeRequest): ApiResult[GetTimeResult] = handleDbException {
    import request._

    OkApiResult(Some(GetTimeResult(new Date())))
  }

}

