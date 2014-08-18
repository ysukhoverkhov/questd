package controllers.domain.app.user

import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.mock.Mockito
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import controllers.domain._
import controllers.domain.app.user._
import models.store._
import models.domain._
import models.domain.view._
import models.store.mongo._
import models.store.dao.UserDAO
import components.componentregistry.ComponentRegistry
import java.util.Date
import controllers.domain.app.protocol.ProfileModificationResult
import models.domain.stubCreators._

class ProposeQuestAPISpecs extends BaseAPISpecs {

  def createUser(vip: Boolean) = {

    User(
      id = "user_id",
      profile = Profile(
        questProposalContext = QuestProposalConext(
          approveReward = Assets(1, 2, 3),
          takenTheme = Some(ThemeWithID("theme_id", createThemeStub().info)),
          questProposalCooldown = new Date(Long.MaxValue)),
        publicProfile = PublicProfile(vip = vip),
        rights = Rights.full))
  }

  def createQuest = {
    QuestInfoContent(ContentReference(ContentType.Photo, "", ""), None, "")
  }

  "Propose Quest API" should {

    "Create regular quests for regular users" in context {

      val u = createUser(false)
      val q = createQuest

      user.resetQuestProposal(any, any) returns Some(u)

      val result = api.proposeQuest(ProposeQuestRequest(u, q))

      result.body.get.allowed must beEqualTo(ProfileModificationResult.OK)

      there was one(quest).create(
        Quest(
          id = anyString,
          approveReward = u.profile.questProposalContext.approveReward,
          info = QuestInfo(
            authorId = u.id,
            themeId = u.profile.questProposalContext.takenTheme.get.id,
            content = q,
            vip = false)))
    }

    "Create VIP quests for VIP users" in context {
      val u = createUser(true)
      val q = createQuest

      user.resetQuestProposal(any, any) returns Some(u)

      val result = api.proposeQuest(ProposeQuestRequest(u, q))

      result.body.get.allowed must beEqualTo(ProfileModificationResult.OK)

      there was one(quest).create(
        Quest(
          id = anyString,
          approveReward = u.profile.questProposalContext.approveReward,
          info = QuestInfo(
            authorId = u.id,
            themeId = u.profile.questProposalContext.takenTheme.get.id,
            content = q,
            vip = true)))
    }
  }
}

