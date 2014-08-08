package controllers.domain.app.questsolution

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import controllers.domain._
import controllers.domain.app.user._
import models.store._
import models.domain._
import models.store.mongo._
import controllers.domain.app.quest._

class QuestsSolutionFetchAPISpecs extends BaseAPISpecs {

  "Quest solution Fetch API" should {

    "getFriendsSolutions return solutions for confirmed friends only" in context {

      def createUser(friends: List[Friendship]) = {
        User(friends = friends)
      }

      def createFriend(newid: String) = {
        User(id = newid)
      }

      val f1 = createFriend("f1")
      val f2 = createFriend("f2")

      val u = createUser(List(Friendship(f1.id, FriendshipStatus.Accepted), Friendship(f2.id, FriendshipStatus.Invited)))

      db.solution.allWithParams(List(QuestStatus.InRotation.toString), List(f1.id), Some(1, 2), 0, None, List(), List()) returns List().iterator
      db.solution.allWithParams(List(QuestStatus.InRotation.toString), List(f1.id, f2.id), Some(1, 2), 0, None, List(), List()) returns List().iterator

      val result = api.getFriendsSolutions(GetFriendsSolutionsRequest(u, QuestSolutionStatus.OnVoting, Some(1, 2)))

      there was one(solution).allWithParams(
        List(QuestSolutionStatus.OnVoting.toString),
        List(f1.id),
        Some(1, 2),
        0,
        null,
        null,
        null,
        null)

      there was no(solution).allWithParams(
        List(QuestSolutionStatus.OnVoting.toString),
        List(),
        Some(1, 2),
        0,
        null,
        null,
        null,
        null)
        
      there was no(solution).allWithParams(
        List(QuestSolutionStatus.OnVoting.toString),
        List(f1.id, f2.id),
        Some(1, 2),
        0,
        null,
        null,
        null,
        null)
    }

    "getSolutionsForLikedQuests calls db correctly" in context {

      db.solution.allWithParams(List(QuestStatus.InRotation.toString), List(), Some(1, 2), 0, Some(false), List("1", "2", "3", "4"), List()) returns List().iterator

      
      val liked = List(
          List("1", "2"),
          List("3", "4")) 
      val u = User(history = UserHistory(likedQuestProposalIds = liked))
      val result = api.getSolutionsForLikedQuests(GetSolutionsForLikedQuestsRequest(u, QuestSolutionStatus.OnVoting, Some(1, 2)))

      there was one(solution).allWithParams(
        List(QuestSolutionStatus.OnVoting.toString),
        null,
        Some(1, 2),
        0,
        null,
        null,
        List("1", "2", "3", "4"),
        null)
    }
    
    "getVIPSolutions calls db correctly" in context {

      db.solution.allWithParams(List(QuestStatus.InRotation.toString), List(), Some(1, 2), 0, Some(true), List(), List("a")) returns List().iterator

      val result = api.getVIPSolutions(GetVIPSolutionsRequest(User(), QuestSolutionStatus.OnVoting, Some(1, 2), List("a")))

      there was one(solution).allWithParams(
        List(QuestSolutionStatus.OnVoting.toString),
        null,
        Some(1, 2),
        0,
        Some(true),
        null,
        null,
        List("a"))
    }

    "getAllSolutions calls db correctly" in context {

      db.solution.allWithParams(List(QuestStatus.InRotation.toString), List(), Some(1, 2), 0, None, List(), List("a")) returns List().iterator

      val result = api.getAllSolutions(GetAllSolutionsRequest(QuestSolutionStatus.OnVoting, Some(1, 2), List("a")))

      there was one(solution).allWithParams(
        List(QuestSolutionStatus.OnVoting.toString),
        null,
        Some(1, 2),
        0,
        null,
        null,
        null,
        List("a"))
    }
  }
}


