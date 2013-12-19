package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import play.Logger

case class ShiftDailyResultRequest(user: User)
case class ShiftDailyResultResult()

private[domain] trait DailyResultAPI { this: DBAccessor =>

  /**
   * Get iterator for all users.
   */
  def shiftDailyResult(request: ShiftDailyResultRequest): ApiResult[ShiftDailyResultResult] = handleDbException {
    import request._

    db.user.update(user.copy(privateDailyResults = DailyResult(user.getStartOfCurrentDailyResultPeriod) :: user.privateDailyResults))
    
    OkApiResult(Some(ShiftDailyResultResult()))
  }

}

