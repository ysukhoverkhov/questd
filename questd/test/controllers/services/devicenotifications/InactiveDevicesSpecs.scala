package controllers.services.devicenotifications

import java.util.Date

import akka.actor.Props
import akka.testkit.TestProbe
import akka.util.Timeout
import controllers.services.devicenotifications.apple.AppleInactiveDevices
import org.specs2.mutable._
import testhelpers.specs2support.AkkaTestKitSpecs2Support

import scala.concurrent.duration._

class InactiveDevicesSpecs
  extends Specification {

  "InactiveDevice" should {

    "Asks apple to inactive devices info" in new AkkaTestKitSpecs2Support {
      val fakeApple = TestProbe()
      val subject = system.actorOf(Props(new InactiveDevices with TestActorCreationSupport{override lazy val child = fakeApple.ref}))
      val inactiveDevices = Map("test string" -> new Date)

      within(5.seconds) {
        subject ! InactiveDevices.GetInactiveDevicesRequest

        fakeApple.expectMsg(AppleInactiveDevices.GetAppleInactiveDevicesRequest)
        fakeApple.reply(AppleInactiveDevices.GetAppleInactiveDevicesResult(inactiveDevices))

        expectMsg(InactiveDevices.GetInactiveDevicesResult(InactiveDevices.IOSDevice, inactiveDevices))
      }
    }

    "Returns empty list of devices on exception" in new AkkaTestKitSpecs2Support {
      val fakeApple = TestProbe()
      val subject = system.actorOf(Props(new InactiveDevices with TestActorCreationSupport {
        override implicit val timeout: Timeout = Timeout(1.seconds)
      }))
      val inactiveDevices = Map("test string" -> new Date)

      within(2.seconds) {
        subject ! InactiveDevices.GetInactiveDevicesRequest

        expectMsg(AppleInactiveDevices.GetAppleInactiveDevicesRequest)

        expectMsg(InactiveDevices.GetInactiveDevicesResult(InactiveDevices.IOSDevice, Map.empty))
      }
    }
  }
}
