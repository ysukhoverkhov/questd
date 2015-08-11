package controllers.services.devicenotifications.notifications

import java.net.UnknownHostException
import java.util.Date

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.routing.RoundRobinPool
import com.notnoop.apns.{APNS, ApnsService}
import controllers.services.devicenotifications.notifications.ApplePushNotificationProtocol.ScreenMessage
import play.Logger

object ApplePushNotificationProtocol {
  case class ScreenMessage(deviceToken: String, message: String, badge: Option[Int], sound: Option[String])
}

object ApplePushNotification {
  val props = Props[ApplePushNotification].withRouter(RoundRobinPool(nrOfInstances = 10)) // TODO: perhaps think about replacing it with single instance.
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

      import scala.collection.JavaConversions._

      val payload = APNS.newPayload().alertBody(message).build()
      service.push(deviceToken, payload)

      // TODO: remove unresponding devices periodically.
      val inactiveDevices: Map[String, Date] = service.getInactiveDevices.toMap
      for (deviceToken <- inactiveDevices.keySet) {
        val inactiveAsOf = inactiveDevices.get(deviceToken)
        Logger.error(s"inactive since $inactiveAsOf - $deviceToken")
      }
  }
}
