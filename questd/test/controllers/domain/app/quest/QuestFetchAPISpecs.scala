package controllers.domain.app.quest

import controllers.domain._
import models.domain.quest.QuestStatus
import models.domain.user.User
import models.domain.user.friends.{Friendship, FriendshipStatus}
import testhelpers.domainstubs._
import org.mockito.Matchers.{eq => mEq}

class QuestFetchAPISpecs extends BaseAPISpecs {

  "Quest Fetch API" should {

    "getFriendsQuests return quests for confirmed friends only" in context {
      val f1 = createUserStub(id = "f1")
      val f2 = createUserStub(id = "f2")

      val u = createUserStub(friends = List(Friendship(f1.id, FriendshipStatus.Accepted), Friendship(f2.id, FriendshipStatus.Invited)))

      db.quest.allWithParams(
        status = mEq(List(QuestStatus.InRotation)),
        authorIds = mEq(List(f1.id)),
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = mEq(None),
        ids = mEq(List.empty),
        idsExclude = mEq(List.empty),
        cultureId = any,
        withSolutions = mEq(false)
      ) returns Iterator.empty
      db.quest.allWithParams(
        status = mEq(List(QuestStatus.InRotation)),
        authorIds = mEq(List(f1.id, f2.id)),
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = mEq(None),
        ids = mEq(List.empty),
        idsExclude = mEq(List.empty),
        cultureId = any,
        withSolutions = mEq(false)
      ) returns Iterator.empty

      val result = api.getFriendsQuests(GetFriendsQuestsRequest(
        user = u,
        status = QuestStatus.InRotation,
        levels = Some((1, 2))))

      result must beAnInstanceOf[OkApiResult[GetFriendsQuestsResult]]

      there was one(quest).allWithParams(
        status = mEq(List(QuestStatus.InRotation)),
        authorIds = mEq(List(f1.id)),
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = mEq(List.empty),
        cultureId = mEq(u.demo.cultureId),
        withSolutions = mEq(false))

      there was no(quest).allWithParams(
        status = mEq(List(QuestStatus.InRotation)),
        authorIds = mEq(List.empty),
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = mEq(List.empty),
        cultureId = mEq(u.demo.cultureId),
        withSolutions = mEq(false))

      there was no(quest).allWithParams(
        status = mEq(List(QuestStatus.InRotation)),
        authorIds = mEq(List(f1.id, f2.id)),
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = mEq(List.empty),
        cultureId = mEq(u.demo.cultureId),
        withSolutions = mEq(false))
    }

    "getVIPQuests calls db correctly" in context {

      db.quest.allWithParams(
        status = mEq(List(QuestStatus.InRotation)),
        authorIds = mEq(List.empty),
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = mEq(Some(true)),
        ids = mEq(List.empty),
        idsExclude = mEq(List.empty),
        cultureId = any,
        withSolutions = any
      ) returns Iterator.empty

      val u = createUserStub()
      val result = api.getVIPQuests(GetVIPQuestsRequest(
        user = u,
        status = QuestStatus.InRotation,
        levels = Some((1, 2))))

      result must beAnInstanceOf[OkApiResult[GetVIPQuestsResult]]

      there was one(quest).allWithParams(
        status = mEq(List(QuestStatus.InRotation)),
        authorIds = any,
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = mEq(Some(true)),
        ids = any,
        idsExclude = mEq(List.empty),
        cultureId = mEq(u.demo.cultureId),
        withSolutions = any)
    }

    "getAllQuests calls db correctly" in context {

      db.quest.allWithParams(
        status = mEq(List(QuestStatus.InRotation)),
        authorIds = mEq(List.empty),
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = mEq(None),
        ids = mEq(List.empty),
        idsExclude = mEq(List.empty),
        cultureId = any,
        withSolutions = any
      ) returns List.empty.iterator

      val result = api.getAllQuests(GetAllQuestsRequest(
        user = createUserStub(cultureId = Some("cid")),
        status = QuestStatus.InRotation,
        levels = Some((1, 2)),
        cultureId = Some("cid")))

      result must beAnInstanceOf[OkApiResult[GetAllQuestsResult]]

      there was one(quest).allWithParams(
        status = mEq(List(QuestStatus.InRotation)),
        authorIds = any,
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = any,
        ids = mEq(List.empty),
        idsExclude = mEq(List.empty),
        cultureId = mEq(Some("cid")),
        withSolutions = any)
    }
  }
}

