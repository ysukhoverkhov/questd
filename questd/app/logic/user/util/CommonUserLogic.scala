package logic.user.util

import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime
import logic.{UserLogic, _}

trait CommonUserLogic { this: UserLogic =>

  /**
   * Calculates time of next flip hour Date for user.
   */
  private[user] def nextFlipHourDate = {
    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    (DateTime.now(tz) + 1.day).hour(constants.FlipHour).minute(0).second(0) toDate ()
  }

}
