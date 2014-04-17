package logic.userlogic

import org.specs2.mutable._
import org.specs2.mock.Mockito
import components.APIAccessor
import components.RandomAccessor
import components.random.RandomComponent
import controllers.domain.DomainAPIComponent
import models.store.DatabaseComponent
import controllers.domain.admin._
import logic.LogicBootstrapper

private[userlogic] abstract class BaseUserLogicSpecs extends Specification 
  with Mockito 
  with LogicBootstrapper 
  with APIAccessor
  with RandomAccessor
  
  with RandomComponent
  with DatabaseComponent
  with DomainAPIComponent {
  
  val db = mock[Database]
  val api = mock[DomainAPI]
  val rand = mock[Random]
}
