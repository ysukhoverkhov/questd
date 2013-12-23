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

case class GetDailyResultRequest(user: User)
case class GetDailyResultResult(profile: Profile, hasNewResult: Boolean)

private[domain] trait DailyResultAPI { this: DBAccessor =>

  /**
   * Shifts daily result.
   */
  def shiftDailyResult(request: ShiftDailyResultRequest): ApiResult[ShiftDailyResultResult] = handleDbException {
    import request._

    db.user.update(user.copy(privateDailyResults = DailyResult(user.getStartOfCurrentDailyResultPeriod) :: user.privateDailyResults))
    
    // TODO apply daily rating decrease here.

    OkApiResult(Some(ShiftDailyResultResult()))
  }

  /**
   * Returns moves ready daily results to publick daily results and returns public results to client
   */
  def getDailyResult(request: GetDailyResultRequest): ApiResult[GetDailyResultResult] = handleDbException {
    // Check replace old public daily results with new daily results.
    val (u, newOne) = if (request.user.privateDailyResults.length > 1) {
      val u = request.user.copy(
        privateDailyResults = List(request.user.privateDailyResults.head),
        profile = request.user.profile.copy(
          dailyResults = request.user.privateDailyResults.tail))

      db.user.update(u)
      
      // TODO upply all canges in daily results to profile here.

      (u, true)
    } else {
      (request.user, false)
    }

    OkApiResult(Some(GetDailyResultResult(u.profile, newOne)))
  }

}


