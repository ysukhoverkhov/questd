package controllers.services.devicenotifications.apple

import java.net.UnknownHostException
import java.util.Date

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import controllers.services.devicenotifications.apple.AppleInactiveDevices.{GetAppleInactiveDevicesRequest, GetAppleInactiveDevicesResult}
import controllers.services.devicenotifications.apple.helpers.APNSService

import scala.concurrent.Future


object AppleInactiveDevices {
  val name = "AppleInactiveDevices"
  val props = Props[AppleInactiveDevices]

  case object GetAppleInactiveDevicesRequest
  case class GetAppleInactiveDevicesResult(inactiveDevices: Map[String, Date])
}

/**
 * Notifications for apple
 */
class AppleInactiveDevices extends Actor with APNSService {
  import scala.concurrent.duration._

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 10.seconds) {
    case x: UnknownHostException => Restart
  }

  def receive: Receive = {

    case GetAppleInactiveDevicesRequest =>

      import scala.collection.JavaConversions._
      import akka.pattern.pipe
      import context.dispatcher // get network requests dispatcher here.

      Future {
        GetAppleInactiveDevicesResult(service.getInactiveDevices.toMap)
      } pipeTo sender
  }
}
// FIX: all futures should be run in proper context.
