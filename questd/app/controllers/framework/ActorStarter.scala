package controllers.framework

import controllers.services.devicenotifications.{InactiveDevices, DeviceNotifications}
import play.libs.Akka

/**
 * Starts all actors for now.
 */
class ActorStarter {

  // Creating parent actor for all notification actors.
  val deviceNotifications = Akka.system.actorOf(DeviceNotifications.props, name = DeviceNotifications.name)
  val inactiveDevices = Akka.system.actorOf(InactiveDevices.props, name = InactiveDevices.name)

  val listener = Akka.system.actorOf(UnhandledMessagesListener.props, name = UnhandledMessagesListener.name)
}
