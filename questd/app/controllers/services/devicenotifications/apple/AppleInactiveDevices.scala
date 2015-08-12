package controllers.services.devicenotifications.apple

import java.net.UnknownHostException
import java.util.Date

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.routing.RoundRobinPool
import controllers.services.devicenotifications.apple.AppleInactiveDevices.{GetInactiveDevicesRequest, GetInactiveDevicesResponse}
import controllers.services.devicenotifications.apple.helpers.APNSService


object AppleInactiveDevices {
  val name = "ApplePushNotification"
  val props = Props[AppleInactiveDevices].withRouter(RoundRobinPool(nrOfInstances = 10))

  case class GetInactiveDevicesRequest()
  case class GetInactiveDevicesResponse(inactiveDevices: Map[String, Date])
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

    case GetInactiveDevicesRequest() =>
      import scala.collection.JavaConversions._
      sender ! GetInactiveDevicesResponse(service.getInactiveDevices.toMap)
  }
}
