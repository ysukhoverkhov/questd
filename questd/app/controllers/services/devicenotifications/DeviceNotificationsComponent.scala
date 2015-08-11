package controllers.services.devicenotifications

import play.libs.Akka

// Remove it and create device notification supervisor from parent actor directly.
trait DeviceNotificationsComponent { component =>

  protected val deviceNotifications: DeviceNotifications

  class DeviceNotifications {

    // Creating parent actor for all notification actors.
    val deviceNotifications = Akka.system.actorOf(DeviceNotifications.props, name = DeviceNotifications.name)

  }
}

