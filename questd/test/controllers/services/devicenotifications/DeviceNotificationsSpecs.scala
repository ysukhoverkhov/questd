package controllers.services.devicenotifications

import akka.actor.{ActorRef, Props}
import com.vita.akka.cake.ActorCreationSupport
import controllers.services.devicenotifications.DeviceNotifications.{IOSDevice, Devices}
import controllers.services.devicenotifications.apple.ApplePushNotification
import org.specs2.mutable._
import org.specs2.time.NoTimeConversions
import testhelpers.specs2support.AkkaTestKitSpecs2Support

import scala.concurrent.duration._

class DeviceNotificationsSpecs
  extends Specification
  with NoTimeConversions {

  "DeviceNotifications" should {

    "Asks apple to send notifications" in new AkkaTestKitSpecs2Support {
      private val device1 = IOSDevice("token1")
      private val device2 = IOSDevice("token2")
      private val message = "message"

      within(1.second) {
        system.actorOf(Props(new DeviceNotifications with ActorCreationSupport {
          override def createChild(props: Props, name: String): ActorRef = {
            testActor
          }
        }))
      } ! DeviceNotifications.PushMessage(Devices(Set(device2, device1)), message)

      expectMsgAllOf(
        ApplePushNotification.ScreenMessage(device1.deviceToken, message, None, None),
        ApplePushNotification.ScreenMessage(device2.deviceToken, message, None, None))
    }
  }
}

