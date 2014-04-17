package controllers.domain

import org.specs2.mutable._
import org.specs2.mock.Mockito
import components.APIAccessor
import components.RandomAccessor
import components.random.RandomComponent
import models.store.DatabaseComponent
import controllers.domain.admin._
import models.store.dao._

private[domain] abstract class BaseAPISpecs extends Specification 
  with RandomComponent
  with DatabaseComponent
  with DomainAPIComponent
  with Mockito {
  
  isolated

  // Constructing our cake
  val db = mock[Database]
  val user = mock[UserDAO]
  val quest = mock[QuestDAO]
  
  val rand = mock[Random]
  
  val api = new DomainAPI
  // End constructing

  object context extends org.specs2.mutable.Before {
    def before = {
      db.user returns user
      db.quest returns quest
    } 
  }
  
}

