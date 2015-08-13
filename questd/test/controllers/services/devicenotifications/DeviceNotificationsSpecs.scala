package controllers.services.devicenotifications

import akka.actor.{Actor, Props}
import org.specs2.mutable._
import org.specs2.time.NoTimeConversions
import testhelpers.specs2support.AkkaTestKitSpecs2Support

import scala.concurrent.duration._

class DeviceNotificationsSpecs
  extends Specification
  with NoTimeConversions {

  "Functions should" should {

    "questCreationPeriod" in new AkkaTestKitSpecs2Support {
      within(1.second) {
        system.actorOf(Props(new Actor {
          def receive = { case x => sender ! x }
        })) ! "hallo"

        expectMsgType[String] must be equalTo "hallo"
      }

    }
  }
}

