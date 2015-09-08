package logic.user

import controllers.domain.app.protocol.ProfileModificationResult
import logic.BaseLogicSpecs
import models.domain.common.{Assets, ContentType}
import models.domain.user.profile.Rights
import testhelpers.domainstubs._

class SolvingQuestSpecs extends BaseLogicSpecs {

  "User Logic for solving quests" should {

    "Do not allow solving of quests without rights" in {
      applyConfigMock()

      val user = createUserStub(rights = Rights.none)
      val q = createQuestStub()

      val rv = user.canSolveQuest(ContentType.Photo, q)

      rv must beEqualTo(ProfileModificationResult.NotEnoughRights)
    }

    "Do not allow solving of quests without money" in {
      applyConfigMock()

      val q = createQuestStub(solveCost = Assets(100, 0, 0))
      val tl = List(createTimeLineEntryStub(objectId = q.id))
      val user = createUserStub(assets = Assets(), timeLine = tl)

      val rv = user.canSolveQuest(ContentType.Photo, q)

      rv must beEqualTo(ProfileModificationResult.NotEnoughAssets)
    }

    "Do not allow solving of own quests" in {
      applyConfigMock()

      val questId = "qid"
      val tl = List(createTimeLineEntryStub(objectId = questId))
      val user = createUserStub(timeLine = tl)
      val q = createQuestStub(id = questId, authorId = user.id)

      val rv = user.canSolveQuest(ContentType.Photo, q)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do not allow solving of quests already solved" in {
      applyConfigMock()

      val questId = "qid"
      val tl = List(createTimeLineEntryStub(objectId = questId))
      val user = createUserStub(timeLine = tl, solvedQuests = Map(questId -> "sid"))
      val q = createQuestStub(id = questId)

      val rv = user.canSolveQuest(ContentType.Photo, q)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Allow creating of quests in normal situations" in {
      applyConfigMock()

      val q = createQuestStub(solveCost = Assets(100, 0, 0))
      val tl = List(createTimeLineEntryStub(objectId = q.id))
      val user = createUserStub(assets = Assets(100, 0, 0), timeLine = tl)

      val rv = user.canSolveQuest(ContentType.Photo, q)

      rv must beEqualTo(ProfileModificationResult.OK)
    }
  }
}

