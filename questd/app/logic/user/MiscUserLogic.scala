package logic.user

import com.github.nscala_time.time.Imports._
import logic.{constants, UserLogic, functions}
import models.domain.user.dailyresults.DailyResult
import models.domain.user.profile.Gender
import org.joda.time.DateTime


/**
 * user logic what can't be addressed to one of existing types.
 */
trait MiscUserLogic { this: UserLogic =>

  /**
   * Get amount of rating required to get to next level.
   */
  def ratingToNextLevel: Int = {
    functions.ratToGainLevel(user.profile.publicProfile.level + 1)
  }

  /**
   * Check is current user an active user.
   */
  def isActive = {
    val activeDays = api.config(api.DefaultConfigParams.ActiveUserDays).toInt

    user.auth.lastLogin match {
      case None => false
      case Some(d) =>
        (new DateTime(d) + activeDays.days) > DateTime.now
    }
  }

  /**
   * Check is the user has complete bio.
   */
  def bioComplete = {
    user.demo.cultureId.isDefined && user.profile.publicProfile.bio.gender != Gender.Unknown
  }

  /**
   * Returns initialized version of the user
   */
  def initialized = {
    user.copy(
      profile = user.profile.copy(
        rights = calculateRights,
        ratingToNextLevel = ratingToNextLevel
      ),
      privateDailyResults = List(DailyResult(
        getStartOfCurrentDailyResultPeriod)
      ))
  }

  /**
   * Calculates time of next flip hour Date for user.
   */
  private[user] def nextFlipHourDate = {
    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    (DateTime.now(tz) + 1.day).hour(constants.FlipHour).minute(0).second(0) toDate ()
  }

  /**
   * Is the user has night now.
   */
  def hasNightHoursNow = {
    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    val userLocalTime = DateTime.now(tz)

    (userLocalTime.hourOfDay().get > constants.DayStartHour) && (userLocalTime.hourOfDay().get < constants.DayEndHour)
  }
}
