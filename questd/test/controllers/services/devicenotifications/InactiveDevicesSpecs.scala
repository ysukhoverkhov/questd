package controllers.services.devicenotifications

import java.util.Date

import akka.actor.Props
import akka.testkit.TestProbe
import controllers.services.devicenotifications.apple.AppleInactiveDevices
import org.specs2.mutable._
import org.specs2.time.NoTimeConversions
import testhelpers.specs2support.AkkaTestKitSpecs2Support

import scala.concurrent.duration._

class InactiveDevicesSpecs
  extends Specification
  with NoTimeConversions {

  "InactiveDevice" should {

    "Asks apple to inactive devices info" in new AkkaTestKitSpecs2Support {
      val fakeApple = TestProbe("FakeAppleInactiveDevices")
      val subject = system.actorOf(Props(new InactiveDevices with TestActorCreationSupport{override lazy val child = fakeApple.ref}))
      val inactiveDevices = Map("test string" -> new Date)

      within(5.seconds) {
        subject ! InactiveDevices.GetInactiveDevicesRequest

        fakeApple.expectMsg(AppleInactiveDevices.GetAppleInactiveDevicesRequest)
        fakeApple.reply(AppleInactiveDevices.GetAppleInactiveDevicesResult(inactiveDevices))

        expectMsg(InactiveDevices.GetInactiveDevicesResult(InactiveDevices.IOSDevice, inactiveDevices))
      }
    }
  }
}
