package testhelpers.specs2support

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
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
}
