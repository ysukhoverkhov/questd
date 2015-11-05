package controllers.domain.app.user

import controllers.domain.{BaseAPISpecs, OkApiResult}
import models.domain.quest.QuestStatus
import testhelpers.domainstubs._

class VoteQuestAPISpecs extends BaseAPISpecs {

  "Vote Quest API" should {
    "hideOwnQuest hides own quests only" in context {
      val u = createUserStub()
      val q = createQuestStub(authorId = u.id + "qwe")

      db.quest.readById(any) returns Some(q)

      val result = api.hideOwnQuest(HideOwnQuestRequest(u, q.id))

      result must beAnInstanceOf[OkApiResult[HideOwnQuestResult]]
      result.body.get.allowed must beEqualTo(HideOwnQuestCode.NotOwnQuest)
    }

    "hideOwnQuest report missing quests correctly" in context {
      val u = createUserStub()
      val q = createQuestStub(authorId = u.id + "qwe")

      db.quest.readById(any) returns None

      val result = api.hideOwnQuest(HideOwnQuestRequest(u, q.id))

      result must beAnInstanceOf[OkApiResult[HideOwnQuestResult]]
      result.body.get.allowed must beEqualTo(HideOwnQuestCode.QuestNotFound)
    }

    "hideOwnQuest in rotation only" in context {
      val u = createUserStub()
      val q = createQuestStub(authorId = u.id, status = QuestStatus.AdminBanned)

      db.quest.readById(any) returns Some(q)

      val result = api.hideOwnQuest(HideOwnQuestRequest(u, q.id))

      result must beAnInstanceOf[OkApiResult[HideOwnQuestResult]]
      result.body.get.allowed must beEqualTo(HideOwnQuestCode.QuestNotInRotation)
    }

    "hideOwnQuest works" in context {
      val u = createUserStub()
      val q = createQuestStub(authorId = u.id)

      db.quest.readById(any) returns Some(q)

      val result = api.hideOwnQuest(HideOwnQuestRequest(u, q.id))

      result must beAnInstanceOf[OkApiResult[HideOwnQuestResult]]
      result.body.get.allowed must beEqualTo(HideOwnQuestCode.OK)
    }
  }
}
