package controllers.domain.app.quest

import controllers.domain._
import models.domain._

class QuestFetchAPISpecs extends BaseAPISpecs {

  "Quest Fetch API" should {

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

      db.quest.allWithParams(List(QuestStatus.InRotation.toString), List(f1.id), Some(1, 2), 0, None, List(), List()) returns List().iterator
      db.quest.allWithParams(List(QuestStatus.InRotation.toString), List(f1.id, f2.id), Some(1, 2), 0, None, List(), List()) returns List().iterator

      val result = api.getFriendsQuests(GetFriendsQuestsRequest(u, QuestStatus.InRotation, Some(1, 2)))

      there was one(quest).allWithParams(
        List(QuestStatus.InRotation.toString),
        List(f1.id),
        Some(1, 2),
        0,
        null,
        null,
        null,
        null)

      there was no(quest).allWithParams(
        List(QuestStatus.InRotation.toString),
        List(),
        Some(1, 2),
        0,
        null,
        null,
        null,
        null)

      there was no(quest).allWithParams(
        List(QuestStatus.InRotation.toString),
        List(f1.id, f2.id),
        Some(1, 2),
        0,
        null,
        null,
        null,
        null)
    }

    "getLikedQuests calls db correctly" in context {

      db.quest.allWithParams(List(QuestStatus.InRotation.toString), List(), Some(1, 2), 0, Some(false), List("1", "2", "3", "4"), List()) returns List().iterator


      val liked = List(
          List("1", "2"),
          List("3", "4"))
      val u = User(history = UserHistory(likedQuestProposalIds = liked))
      val result = api.getLikedQuests(GetLikedQuestsRequest(u, QuestStatus.InRotation, Some(1, 2)))

      there was one(quest).allWithParams(
        List(QuestStatus.InRotation.toString),
        null,
        Some(1, 2),
        0,
        null,
        List("1", "2", "3", "4"),
        null,
        null)
    }

    "getVIPQuests calls db correctly" in context {

      db.quest.allWithParams(List(QuestStatus.InRotation.toString), List(), Some(1, 2), 0, Some(true), List(), List("a")) returns List().iterator

      val result = api.getVIPQuests(GetVIPQuestsRequest(User(), QuestStatus.InRotation, Some(1, 2), List("a")))

      there was one(quest).allWithParams(
        List(QuestStatus.InRotation.toString),
        null,
        Some(1, 2),
        0,
        Some(true),
        null,
        List("a"),
        null)
    }

    "getAllQuests calls db correctly" in context {

      db.quest.allWithParams(List(QuestStatus.InRotation.toString), List(), Some(1, 2), 0, None, List(), List("a")) returns List().iterator

      val result = api.getAllQuests(GetAllQuestsRequest(QuestStatus.InRotation, Some(1, 2), List("a")))

      there was one(quest).allWithParams(
        List(QuestStatus.InRotation.toString),
        null,
        Some(1, 2),
        0,
        null,
        null,
        List("a"),
        null)
    }
  }
}


