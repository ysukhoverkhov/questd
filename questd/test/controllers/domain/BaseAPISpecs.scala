package controllers.domain

import org.specs2.mutable._
import org.specs2.mock.Mockito
import components.APIAccessor
import components.RandomAccessor
import components.random.RandomComponent
import models.store.DatabaseComponent
import controllers.domain.admin._
import models.store.dao._
import models.domain.admin.Configuration
import controllers.domain.libs.facebook.FacebookComponent

private[domain] abstract class BaseAPISpecs extends Specification 
  with RandomComponent
  with DatabaseComponent
  with DomainAPIComponent
  with FacebookComponent
  with Mockito {
  
  isolated

  // Constructing our cake
  val fb = mock[Facebook]
  val db = mock[Database]
  val user = mock[UserDAO]
  val quest = mock[QuestDAO]
  val solution = mock[QuestSolutionDAO]
  val config = mock[ConfigDAO]
  val tutorialTask = mock[TutorialTaskDAO]
  
  val rand = mock[Random]
  
  val api = spy(new DomainAPI)
  // End constructing

  def mockConfiguration: Configuration = {
    Configuration(Map())
  }
  
  object context extends org.specs2.mutable.Before {
    def before = {
      db.user returns user
      db.quest returns quest
      db.solution returns solution
      db.config returns config
      db.tutorialTask returns tutorialTask
      
      config.readConfig returns mockConfiguration
    } 
  }
  
}

