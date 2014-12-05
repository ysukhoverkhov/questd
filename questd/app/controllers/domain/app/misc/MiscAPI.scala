package controllers.domain.app.misc

import java.util.Date

import components._
import controllers.domain._
import controllers.domain.helpers._
import models.domain._

case class GetTimeRequest(user: User)
case class GetTimeResult(time: Date)

private[domain] trait MiscAPI { this: DBAccessor =>

  /**
   * Get server's time.
   */
  def getTime(request: GetTimeRequest): ApiResult[GetTimeResult] = handleDbException {
    OkApiResult(GetTimeResult(new Date()))
  }
}

