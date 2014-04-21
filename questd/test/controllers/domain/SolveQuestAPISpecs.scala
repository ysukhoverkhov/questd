package controllers.domain

import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.mock.Mockito
import org.junit.runner._
import play.Logger
import play.api.test._
import play.api.test.Helpers._
import controllers.domain._
import controllers.domain.app.user._
import models.store._
import models.domain._
import models.store.mongo._
import models.store.dao.UserDAO
import controllers.domain.libs.facebook.UserFB
import components.random.RandomComponent

class SolveQuestAPISpecs extends BaseAPISpecs {

  "Solve Quest API" should {

    "getFriendsQuests return quests for confirmed friends only" in context {

      def createUser(friends: List[Friendship]) = {
        User(friends = friends)
      }

      def createFriend(newid: String) = {
        User(id = newid)
      }

      val f1 = createFriend("f1")
      val f2 = createFriend("f2")

      val u = createUser(List(Friendship(f1.id, FriendshipStatus.Accepted.toString), Friendship(f2.id, FriendshipStatus.Invited.toString)))

      db.quest.allWithParams(Some(QuestStatus.InRotation.toString), List(f1.id), Some(1, 2), 0, None, List(), List()) returns List().iterator
      db.quest.allWithParams(Some(QuestStatus.InRotation.toString), List(f1.id, f2.id), Some(1, 2), 0, None, List(), List()) returns List().iterator

      val result = api.getFriendsQuests(GetFriendsQuestsRequest(u, 1, 2))

      there was one(quest).allWithParams(
        Some(QuestStatus.InRotation.toString),
        List(f1.id),
        Some(1, 2),
        0,
        null,
        null,
        null)

      there was no(quest).allWithParams(
        Some(QuestStatus.InRotation.toString),
        List(),
        Some(1, 2),
        0,
        null,
        null,
        null)
        
      there was no(quest).allWithParams(
        Some(QuestStatus.InRotation.toString),
        List(f1.id, f2.id),
        Some(1, 2),
        0,
        null,
        null,
        null)
    }

    "getLikedQuests calls db correctly" in context {

      db.quest.allWithParams(Some(QuestStatus.InRotation.toString), List(), Some(1, 2), 0, Some(false), List("1", "2", "3", "4"), List()) returns List().iterator

      
      val liked = List(
          List("1", "2"),
          List("3", "4")) 
      val u = User(history = UserHistory(likedQuestProposalIds = liked))
      val result = api.getLikedQuests(GetLikedQuestsRequest(u, 1, 2))

      there was one(quest).allWithParams(
        Some(QuestStatus.InRotation.toString),
        null,
        Some(1, 2),
        0,
        null,
        List("1", "2", "3", "4"),
        null)
    }
    
    "getVIPQuests calls db correctly" in context {

      db.quest.allWithParams(Some(QuestStatus.InRotation.toString), List(), Some(1, 2), 0, Some(true), List(), List("a")) returns List().iterator

      val result = api.getVIPQuests(GetVIPQuestsRequest(User(), 1, 2, List("a")))

      there was one(quest).allWithParams(
        Some(QuestStatus.InRotation.toString),
        null,
        Some(1, 2),
        0,
        Some(true),
        null,
        List("a"))
    }
  }
}


