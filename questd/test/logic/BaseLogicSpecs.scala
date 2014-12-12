package logic

import components.{APIAccessor, RandomAccessor}
import components.random.RandomComponent
import controllers.domain.DomainAPIComponent
import controllers.sn.component.SocialNetworkComponent
import models.store.DatabaseComponent
import org.specs2.mock.Mockito
import org.specs2.mutable._

private[logic] abstract class BaseLogicSpecs extends Specification
  with Mockito
  with LogicBootstrapper
  with APIAccessor
  with RandomAccessor

  with RandomComponent
  with DatabaseComponent
  with DomainAPIComponent
  with SocialNetworkComponent {

  isolated

  val sn = mock[SocialNetwork]
  val db = mock[Database]
  val api = mock[DomainAPI]
  val rand = mock[Random]
}
