package controllers.domain

import components.random.RandomComponent
import controllers.sn.component.SocialNetworkComponent
import models.domain.admin.Configuration
import models.store.DatabaseComponent
import models.store.dao._
import org.specs2.mock.Mockito
import org.specs2.mutable._

private[domain] abstract class BaseAPISpecs
  extends Specification
  with RandomComponent
  with DatabaseComponent
  with DomainAPIComponent
  with SocialNetworkComponent
  with Mockito {

  isolated

  // Constructing our cake
  val sn = mock[SocialNetwork]
  val db = mock[Database]
  val user = mock[UserDAO]
  val quest = mock[QuestDAO]
  val solution = mock[QuestSolutionDAO]
  val config = mock[ConfigDAO]
  val tutorialTask = mock[TutorialTaskDAO]
  val culture = mock[CultureDAO]
  val theme = mock[ThemeDAO]

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
      db.culture returns culture
      db.theme returns theme

      config.readConfig returns mockConfiguration
    }
  }

}

