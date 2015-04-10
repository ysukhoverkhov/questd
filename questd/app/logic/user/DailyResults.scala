package logic.user

import java.util.Date

import com.github.nscala_time.time.Imports._
import logic._
import org.joda.time.DateTime

trait DailyResults { this: UserLogic =>

  /**
   *
   */
  def getStartOfCurrentDailyResultPeriod: Date = {
    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    DateTime.now(tz).hour(constants.FlipHour).minute(0).second(0) toDate ()
  }
}
