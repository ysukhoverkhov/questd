package logic.user

import java.util.Date
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

import play.Logger

import logic._
import logic.constants._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import models.domain.base._
import models.domain.ContentType._
import controllers.domain.admin._
import controllers.domain._

trait DailyResults { this: UserLogic =>

  /**
   * 
   */
  def getStartOfCurrentDailyResultPeriod: Date = {
    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    DateTime.now(tz).hour(constants.flipHour).minute(0).second(0) toDate ()
  }

  /**
   * Tells cost of regular daily rating decrease.
   */
  def dailyAssetsDecrease = {
    Assets(rating = dailyRatingDecrease(user.profile.publicProfile.level)) clampTop (user.profile.assets)
  }

}