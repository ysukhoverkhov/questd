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
        authorIdsExclude = List(),
        levels = None,
        skip = 0,
        vip = Some(false),
        ids = List(),
        idsExclude = List(),
        cultureId = None) returns List().iterator

      val result = api.getMyQuests(GetMyQuestsRequest(u, QuestStatus.InRotation))

      result must beAnInstanceOf[OkApiResult[GetMyQuestsResult]]
      there was one(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(u.id),
        authorIdsExclude = null,
        levels = null,
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = null,
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

      db.quest.allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(f1.id),
        authorIdsExclude = List(),
        levels = Some(1, 2),
        skip = 0,
        vip = None,
        ids = List(),
        idsExclude = List()
      ) returns List().iterator
      db.quest.allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(f1.id, f2.id),
        levels = Some(1, 2),
        skip = 0,
        vip = None,
        ids = List(),
        idsExclude = List()
      ) returns List().iterator

      val result = api.getFriendsQuests(GetFriendsQuestsRequest(
        user = u,
        status = QuestStatus.InRotation,
        levels = Some(1, 2)))

      there was one(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(f1.id),
        authorIdsExclude = List(),
        levels = Some(1, 2),
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = List(),
        cultureId = u.demo.cultureId)

      there was no(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(),
        authorIdsExclude = List(),
        levels = Some(1, 2),
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = List(),
        cultureId = u.demo.cultureId)

      there was no(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(f1.id, f2.id),
        authorIdsExclude = List(),
        levels = Some(1, 2),
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = List(),
        cultureId = u.demo.cultureId)
    }

    "getLikedQuests calls db correctly" in context {
      db.quest.allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(),
        authorIdsExclude = List(),
        levels = Some(1, 2),
        skip = 0,
        vip = Some(false),
        ids = List("1", "2", "3", "4"),
        idsExclude = List()
      ) returns List().iterator

      val liked = List("1", "2", "3", "4")
      val u = createUserStub(
        timeLine = liked.map(id => createTimeLineEntryStub(objectId = id, objectType = TimeLineType.Quest, ourVote = Some(ContentVote.Cool)))
      )

      val result = api.getLikedQuests(GetLikedQuestsRequest(
        user = u,
        status = QuestStatus.InRotation,
        levels = Some(1, 2)))

      result must beAnInstanceOf[OkApiResult[GetLikedQuestsResult]]
      there was one(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = null,
        authorIdsExclude = List(),
        levels = Some(1, 2),
        skip = 0,
        vip = null,
        ids = List("1", "2", "3", "4"),
        idsExclude = List(),
        cultureId = u.demo.cultureId)
    }

    "getVIPQuests calls db correctly" in context {

      db.quest.allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(),
        authorIdsExclude = List(),
        levels = Some(1, 2),
        skip = 0,
        vip = Some(true),
        ids = List(),
        idsExclude = List()
      ) returns List().iterator

      val u = createUserStub()
      val result = api.getVIPQuests(GetVIPQuestsRequest(
        user = u,
        status = QuestStatus.InRotation,
        levels = Some(1, 2)))

      there was one(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = null,
        authorIdsExclude = List(),
        levels = Some(1, 2),
        skip = 0,
        vip = Some(true),
        ids = null,
        idsExclude = List(),
        cultureId = u.demo.cultureId)
    }

    "getAllQuests calls db correctly" in context {

      db.quest.allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(),
        authorIdsExclude = List(),
        levels = Some(1, 2),
        skip = 0,
        vip = None,
        ids = List(),
        idsExclude = List()
      ) returns List().iterator

      val result = api.getAllQuests(GetAllQuestsRequest(
        user = createUserStub(cultureId = "cid"),
        status = QuestStatus.InRotation,
        levels = Some(1, 2)))

      there was one(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = null,
        authorIdsExclude = List(),
        levels = Some(1, 2),
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = List(),
        cultureId = Some("cid"))
    }
  }
}

