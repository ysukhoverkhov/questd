package controllers.services.devicenotifications

import akka.actor.{ActorRef, ActorSystem}
import com.vita.akka.cake.ActorSystemCreationSupport
import com.vita.akka.component.{ComponentRouterSimpleCreationSupport, RoutedActorsComponent}

/**
 * Component for device notifications actors.
 *
 * Created by Yury on 01.10.2015.
 */
class DeviceNotificationsComponent(protected val system: ActorSystem)
  extends RoutedActorsComponent(system)
  with ActorSystemCreationSupport
  with ComponentRouterSimpleCreationSupport {

  // Creating parent actor for all notification actors.
  def routes: Map[Class[_], ActorRef] =
    Map(
      DeviceNotifications.PushMessage.getClass ->
        createActor(DeviceNotifications.props, name = DeviceNotifications.name),

      InactiveDevices.GetInactiveDevicesRequest.getClass ->
        createActor(InactiveDevices.props, name = InactiveDevices.name)
    )
}
