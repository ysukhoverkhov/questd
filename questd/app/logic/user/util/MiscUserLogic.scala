package logic.user.util
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._
import logic._
import logic.constants._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import models.domain.base._
import models.domain.ContentType._
import controllers.domain.admin._
import controllers.domain._
import logic.UserLogic

trait MiscUserLogic { this: UserLogic =>

  /**
   * Get amount of rating required to get to next level.
   */
  def ratingToNextLevel: Int = {
    ratToGainLevel(user.profile.publicProfile.level + 1)
  }

  /**
   * Check is current user an active user.
   */
  def userActive = {
    val activeDays = api.config(api.ConfigParams.ActiveUserDays).toInt

    user.auth.lastLogin match {
      case None => false
      case Some(d) => {
        (new DateTime(d) + activeDays.days) > (DateTime.now)
      }
    }
  }
  
  /**
   * Calculates time of next flip hour Date for user.
   */
  def getNextFlipHourDate = {
    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    (DateTime.now(tz) + 1.day).hour(constants.FlipHour).minute(0).second(0) toDate ()
  }
}
