package testhelpers.specs2support

import akka.actor.{ActorRef, Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.vita.akka.cake.ActorCreationSupport
import org.specs2.mutable._

/**
 * Adds ability to run akka TestKit tests in specs2 environment.
 *
 * Created by Yury on 13.08.2015.
 */
abstract class AkkaTestKitSpecs2Support
  extends TestKit(ActorSystem())
  with After
  with ImplicitSender {

  def after = system.terminate()

  trait TestActorCreationSupport extends ActorCreationSupport {
    override def createChild(props: Props, name: String): ActorRef = testActor
  }
}
