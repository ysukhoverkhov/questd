package controllers.services.devicenotifications

import akka.actor.{Actor, Props}
import akka.routing.RoundRobinPool
import com.vita.akka.cake.ActorContextCreationSupport
import controllers.services.devicenotifications.DeviceNotifications.{IOSDevice, MobileDestination, PushMessage, WatchDestination}
import controllers.services.devicenotifications.apple.ApplePushNotification

object DeviceNotifications {
  val name = "DeviceNotifications"
  val props = Props(classOf[DeviceNotifications]).withRouter(RoundRobinPool(nrOfInstances = 1)) // FIX: perhaps think about replacing it with single instance.

  // --- Protocol ---
  sealed trait Device
  case class IOSDevice(deviceToken: String) extends Device {
    override def equals(obj: scala.Any): Boolean = obj match {
      case IOSDevice(dt) => deviceToken == dt
      case x => false
    }

    override val hashCode: Int = deviceToken.hashCode()
  }

  case class Devices(devices: Set[Device]) extends AnyVal {
    def ::(device: Device) = Devices(devices + device)
    def foreach[U](f: Device => U): Unit = devices.foreach(f)
  }
  object Devices {
    val empty = Devices(Set.empty)
  }

  sealed trait Destination
  case object MobileDestination extends Destination
  case object WatchDestination extends Destination

  case class PushMessage(devices: Devices, message: String, badge: Option[Int], sound: Option[String], destinations: Seq[Destination])
}


/**
 * Root notifications actor
 *
 * Created by Yury on 11.08.2015.
 */
class DeviceNotifications extends Actor with ActorContextCreationSupport {
  protected val apple = createChild(ApplePushNotification.props, ApplePushNotification.name)

  def receive: Receive = {
    case PushMessage(devices, message, badge, sound, destinations) =>
      devices.foreach {
        case IOSDevice(deviceToken) =>
          destinations.foreach {
            case MobileDestination => apple ! ApplePushNotification.ScreenMessage(deviceToken, message, badge, sound)
            case WatchDestination => // noop for now
          }
      }
  }
}

