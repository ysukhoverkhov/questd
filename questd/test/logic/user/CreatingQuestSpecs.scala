package logic.user

import java.util.Date

import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.config._ConfigParams
import logic.BaseLogicSpecs
import models.domain.Rights
import models.domain.admin.ConfigSection
import testhelpers.domainstubs._

class CreatingQuestSpecs extends BaseLogicSpecs {


  "User Logic for creating quests" should {

    "Do not allow creation of quests with very long description" in {
      api.config returns createStubConfig

      val user = createUserStub(questCreationCoolDown = new Date(0))
      val veryLongDescription = "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"
      val qic = createQuestStub().info.content.copy(description = veryLongDescription)

      val rv = user.canCreateQuest(qic)

      rv must beEqualTo(ProfileModificationResult.LimitExceeded)
    }

    "Do not allow creation of quests on cool down" in {
      api.config returns createStubConfig

      val user = createUserStub(questCreationCoolDown = new Date(Long.MaxValue))
      val qic = createQuestStub().info.content

      val rv = user.canCreateQuest(qic)

      rv must beEqualTo(ProfileModificationResult.CoolDown)
    }

    "Do not allow creation of quests without rights" in {
      api.config returns createStubConfig

      val user = createUserStub(rights = Rights.none, questCreationCoolDown = new Date(0))
      val qic = createQuestStub().info.content

      val rv = user.canCreateQuest(qic)

      rv must beEqualTo(ProfileModificationResult.NotEnoughRights)
    }

    "Allow creating of quests in normal situations" in {
      api.config returns createStubConfig

      val user = createUserStub(rights = Rights.full, questCreationCoolDown = new Date(0))
      val qic = createQuestStub().info.content

      val rv = user.canCreateQuest(qic)

      rv must beEqualTo(ProfileModificationResult.OK)
    }
  }
}

