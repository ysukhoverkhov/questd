package controllers.services.devicenotifications

import java.util.Date

import akka.actor.{Actor, Props}
import akka.pattern.AskTimeoutException
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.vita.akka.cake.ActorContextCreationSupport
import controllers.services.devicenotifications.InactiveDevices.{IOSDevice, GetInactiveDevicesResult}
import controllers.services.devicenotifications.apple.AppleInactiveDevices.{GetInactiveDevicesRequest, GetAppleInactiveDevicesResult}
import controllers.services.devicenotifications.apple.AppleInactiveDevices
import play.Logger

object InactiveDevices {
  val name = "InactiveDevices"
  val props = Props(classOf[InactiveDevices]).withRouter(RoundRobinPool(nrOfInstances = 1)) // FIX: perhaps think about replacing it with single instance.

  // --- Protocol ---
  sealed trait Device
  case object IOSDevice extends Device

  case object GetInactiveDevicesRequest
  case class GetInactiveDevicesResult(deviceType: Device, inactiveDevices: Map[String, Date])
}


/**
 * Root notifications actor
 *
 * Created by Yury on 11.08.2015.
 */
class InactiveDevices extends Actor with ActorContextCreationSupport {
  val appleInactiveDevices = createChild(AppleInactiveDevices.props, AppleInactiveDevices.name)

  def receive: Receive = {
    case GetInactiveDevicesRequest =>
      import akka.pattern.{ask, pipe}
      import context.dispatcher
      import scala.concurrent.duration._

      implicit val timeout = Timeout(30.seconds)

      (appleInactiveDevices ? GetInactiveDevicesRequest).mapTo[GetAppleInactiveDevicesResult] map { result =>
        GetInactiveDevicesResult(IOSDevice, result.inactiveDevices)
      } recover {
        case e: AskTimeoutException =>
          Logger.error(s"Timeout while asking AppleInactiveDevices ? GetInactiveDevicesRequest", e)
          GetInactiveDevicesResult(IOSDevice, Map.empty)
      } pipeTo sender
  }
}

// TODO: Actor#unhandled(Any)
