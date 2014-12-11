package controllers.domain.app.quest

import controllers.domain._
import models.domain._
import testhelpers.domainstubs._

class QuestFetchAPISpecs extends BaseAPISpecs {

  "Quest Fetch API" should {

    "getMyQuests calls db correctly" in context {

      val u = createUserStub()

      db.quest.allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(u.id),
        levels = None,
        skip = 0,
        vip = Some(false),
        ids = List(),
        cultureId = None) returns List().iterator

      val result = api.getMyQuests(GetMyQuestsRequest(u, QuestStatus.InRotation))

      result must beAnInstanceOf[OkApiResult[GetMyQuestsResult]]
      there was one(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(u.id),
        levels = null,
        skip = 0,
        vip = null,
        ids = null,
        cultureId = null)
    }

    "getFriendsQuests return quests for confirmed friends only" in context {

      def createUser(friends: List[Friendship]) = {
        User(friends = friends)
      }

      def createFriend(newid: String) = {
        User(id = newid)
      }

      val f1 = createFriend("f1")
      val f2 = createFriend("f2")

      val u = createUser(List(Friendship(f1.id, FriendshipStatus.Accepted), Friendship(f2.id, FriendshipStatus.Invited)))

      db.quest.allWithParams(List(QuestStatus.InRotation), List(f1.id), Some(1, 2), 0, None, List()) returns List().iterator
      db.quest.allWithParams(List(QuestStatus.InRotation), List(f1.id, f2.id), Some(1, 2), 0, None, List()) returns List().iterator

      val result = api.getFriendsQuests(GetFriendsQuestsRequest(u, QuestStatus.InRotation, Some(1, 2)))

      there was one(quest).allWithParams(
        List(QuestStatus.InRotation),
        List(f1.id),
        Some(1, 2),
        0,
        null,
        null,
        u.demo.cultureId)

      there was no(quest).allWithParams(
        List(QuestStatus.InRotation),
        List(),
        Some(1, 2),
        0,
        null,
        null,
        u.demo.cultureId)

      there was no(quest).allWithParams(
        List(QuestStatus.InRotation),
        List(f1.id, f2.id),
        Some(1, 2),
        0,
        null,
        null,
        u.demo.cultureId)
    }

    "getLikedQuests calls db correctly" in context {
      db.quest.allWithParams(List(QuestStatus.InRotation), List(), Some(1, 2), 0, Some(false), List("1", "2", "3", "4")) returns List().iterator

      val liked = List("1", "2", "3", "4")
      val u = createUserStub(
        timeLine = liked.map(id => createTimeLineEntryStub(objectId = id, objectType = TimeLineType.Quest, ourVote = Some(ContentVote.Cool)))
      )

      val result = api.getLikedQuests(GetLikedQuestsRequest(u, QuestStatus.InRotation, Some(1, 2)))

      result must beAnInstanceOf[OkApiResult[GetLikedQuestsResult]]
      there was one(quest).allWithParams(
        List(QuestStatus.InRotation),
        null,
        Some(1, 2),
        0,
        null,
        List("1", "2", "3", "4"),
        u.demo.cultureId)
    }

    "getVIPQuests calls db correctly" in context {

      db.quest.allWithParams(List(QuestStatus.InRotation), List(), Some(1, 2), 0, Some(true), List()) returns List().iterator

      val u = createUserStub()
      val result = api.getVIPQuests(GetVIPQuestsRequest(u, QuestStatus.InRotation, Some(1, 2)))

      there was one(quest).allWithParams(
        List(QuestStatus.InRotation),
        null,
        Some(1, 2),
        0,
        Some(true),
        null,
        u.demo.cultureId)
    }

    "getAllQuests calls db correctly" in context {

      db.quest.allWithParams(List(QuestStatus.InRotation), List(), Some(1, 2), 0, None, List()) returns List().iterator

      val result = api.getAllQuests(GetAllQuestsRequest(createUserStub(cultureId = "cid"), QuestStatus.InRotation, Some(1, 2)))

      there was one(quest).allWithParams(
        List(QuestStatus.InRotation),
        null,
        Some(1, 2),
        0,
        null,
        null,
        Some("cid"))
    }
  }
}

