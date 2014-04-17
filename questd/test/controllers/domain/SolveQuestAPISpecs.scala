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

      db.quest.allWithStatusAndUsers(Some(QuestStatus.InRotation.toString), List(f1.id), 0) returns List().iterator
      db.quest.allWithStatusAndUsers(Some(QuestStatus.InRotation.toString), List(f1.id, f2.id), 0) returns List().iterator

      val result = api.getFriendsQuests(GetFriendsQuestsRequest(u))

      there was one(quest).allWithStatusAndUsers(
        Some(QuestStatus.InRotation.toString),
        List(f1.id),
        0)

      there was no(quest).allWithStatusAndUsers(
        Some(QuestStatus.InRotation.toString),
        List(f1.id, f2.id),
        0)
    }
  }
}


