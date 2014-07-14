package controllers.domain.app.misc

import models.domain._
import models.store._
import components._
import controllers.domain._
import controllers.domain.helpers._
import logic._
import controllers.domain.app.protocol.ProfileModificationResult._
import java.util.Date

case class GetTimeRequest(user: User)
case class GetTimeResult(time: Date)

private[domain] trait MiscAPI { this: DBAccessor =>

  /**
   * Get server's time.
   */
  def getTime(request: GetTimeRequest): ApiResult[GetTimeResult] = handleDbException {
    import request._

    OkApiResult(GetTimeResult(new Date()))
  }

}

