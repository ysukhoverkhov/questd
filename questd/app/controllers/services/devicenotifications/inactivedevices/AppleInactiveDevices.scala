package controllers.services.devicenotifications.inactivedevices

import java.net.UnknownHostException
import java.util.Date

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.routing.RoundRobinPool
import com.notnoop.apns.{APNS, ApnsService}
import controllers.services.devicenotifications.notifications.ApplePushNotificationProtocol.{GetInactiveDevicesRequest, GetInactiveDevicesResponse, ScreenMessage}


object ApplePushNotificationProtocol {
  case class ScreenMessage(deviceToken: String, message: String, badge: Option[Int], sound: Option[String])

  case class GetInactiveDevicesRequest()
  case class GetInactiveDevicesResponse(inactiveDevices: Map[String, Date])
}


object ApplePushNotification {
  val name = "ApplePushNotification"
  val props = Props[ApplePushNotification].withRouter(RoundRobinPool(nrOfInstances = 10))
}

/**
 * Notifications for apple
 */
class ApplePushNotification extends Actor {
  import scala.concurrent.duration._

  // TODO: read password from config file.
  // TODO: store in correct place.
  val service: ApnsService =
    APNS.newService()
      .withCert("d:/QMPushDevelop.p12", "123")
      .withSandboxDestination()
      .build()

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 10.seconds) {
    case x: UnknownHostException => Restart
  }

  def receive: Receive = {
    case ScreenMessage(deviceToken, message, badge, sound) =>

      val payload = APNS.newPayload()
//        .badge(3)
//        .customField("secret", "what do you think?")
        .localizedKey(message)
//        .localizedArguments("Jenna", "Frank")
//        .actionKey("Play")
        .build()
      service.push(deviceToken, payload)

    case GetInactiveDevicesRequest() =>
      import scala.collection.JavaConversions._
      sender ! GetInactiveDevicesResponse(service.getInactiveDevices.toMap)
  }
}
