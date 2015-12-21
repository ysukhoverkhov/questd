package testhelpers.specs2support

import akka.actor.{ActorRef, Props, ActorSystem}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
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
  with ImplicitSender
  with DefaultTimeout {

  def after = system.shutdown()

  trait TestActorCreationSupport extends ActorCreationSupport {
    lazy val child = testActor
    protected override def createActor(props: Props, name: String): ActorRef = child
  }
}
