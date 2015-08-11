package controllers.domain

import components.random.RandomComponent
import controllers.services.socialnetworks.component.SocialNetworkComponent
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
  val solution = mock[SolutionDAO]
  val battle = mock[BattleDAO]
  val config = mock[ConfigDAO]
  val tutorialTask = mock[TutorialTaskDAO]
  val tutorial = mock[TutorialDAO]
  val culture = mock[CultureDAO]
  val theme = mock[ThemeDAO]
  val comment = mock[CommentDAO]

  val rand = mock[Random]

  val api = spy(new DomainAPI)
  // End constructing

  def mockConfiguration: Configuration = {
    Configuration(Map.empty)
  }

  object context extends org.specs2.mutable.Before {
    def before = {
      db.user returns user
      db.quest returns quest
      db.solution returns solution
      db.battle returns battle
      db.config returns config
      db.tutorialTask returns tutorialTask
      db.tutorial returns tutorial
      db.culture returns culture
      db.theme returns theme
      db.comment returns comment

      config.readConfig returns mockConfiguration

      api.user2Logic(any)
    }
  }
}

