package controllers.services.devicenotifications

import akka.actor.Props
import controllers.services.devicenotifications.DeviceNotifications.{Devices, IOSDevice}
import controllers.services.devicenotifications.apple.ApplePushNotification
import org.specs2.mutable._
import testhelpers.specs2support.AkkaTestKitSpecs2Support

import scala.concurrent.duration._

class DeviceNotificationsSpecs
  extends Specification {

  "DeviceNotifications" should {

    "Asks apple to send notifications" in new AkkaTestKitSpecs2Support {
      private val device1 = IOSDevice("token1")
      private val device2 = IOSDevice("token2")
      private val message = "message"
      val subject = system.actorOf(Props(new DeviceNotifications with TestActorCreationSupport))

      within(1000.millis) {
        subject ! DeviceNotifications.PushMessage(Devices(Set(device2, device1)), message)

        expectMsgAllOf(
          ApplePushNotification.ScreenMessage(device1.deviceToken, message, None, None),
          ApplePushNotification.ScreenMessage(device2.deviceToken, message, None, None))
      }
    }
  }
}

