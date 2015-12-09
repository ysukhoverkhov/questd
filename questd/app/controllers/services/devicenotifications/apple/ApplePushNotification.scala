package controllers.services.devicenotifications.apple

import java.net.UnknownHostException

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.routing.RoundRobinPool
import com.notnoop.apns.{PayloadBuilder, APNS}
import controllers.services.devicenotifications.apple.ApplePushNotification.ScreenMessage
import controllers.services.devicenotifications.apple.helpers.APNSService


object ApplePushNotification {
  val name = "ApplePushNotification"
  val props = Props[ApplePushNotification].withRouter(RoundRobinPool(nrOfInstances = 10))

  case class ScreenMessage(deviceToken: String, message: String, badge: Option[Int], sound: Option[String])
}

/**
 * Notifications for apple
 */
class ApplePushNotification extends Actor with APNSService {
  import scala.concurrent.duration._

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 10.seconds) {
    case x: UnknownHostException => Restart
  }

  def receive: Receive = {
    case ScreenMessage(deviceToken, message, badge, sound) =>

      def checkAddBadge(builder: PayloadBuilder, badge: Option[Int]) = {
        badge.fold(builder)(builder.badge)
      }

      val payload = checkAddBadge(APNS.newPayload()
//        .customField("secret", "what do you think?")
        .localizedKey(message)
//        .localizedArguments("Jenna", "Frank")
//        .actionKey("Play")
      , badge).build()


      service.push(deviceToken, payload)
  }
}
