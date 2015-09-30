package logic.user

import com.github.nscala_time.time.Imports._
import logic.UserLogic
import org.joda.time.DateTime

trait EventsUserLogic { this: UserLogic =>

  /**
   * Should we send notifications to user right now or ignore current event?
   */
  def shouldSendNotification = {
    (!hasNightHoursNow) && (
      DateTime.now > new DateTime(user.schedules.lastNotificationSentAt) + user.settings
        .notificationsIntervalHours.hours)
  }

}
