package logic.user

import java.util.Date
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._
import logic._
import logic.functions._
import models.domain._

trait DailyResults { this: UserLogic =>

  /**
   *
   */
  def getStartOfCurrentDailyResultPeriod: Date = {
    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    DateTime.now(tz).hour(constants.FlipHour).minute(0).second(0) toDate ()
  }

  /**
   * Everyday player's salary.
   */
  def dailySalary = {
    Assets(coins = dailyCoinsSalary(user.profile.publicProfile.level))
  }
}
