package controllers.domain.app.user

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
import components.random.RandomComponent
import controllers.domain.app.quest._

class ContentAPISpecs extends BaseAPISpecs {

  "Content API" should {

    "Make correct db call in getSolutionsForQuest" in context {
      db.solution.allWithParams(List(QuestSolutionStatus.OnVoting.toString), null, null, 10, null, null, List("qid"), null) returns List[QuestSolution]().iterator

      val result = api.getSolutionsForQuest(GetSolutionsForQuestRequest(User(), "qid", List(QuestSolutionStatus.OnVoting), 2, 5))

      there was one(solution).allWithParams(
        List(QuestSolutionStatus.OnVoting.toString),
        null,
        null,
        10,
        null,
        null,
        List("qid"),
        null)

      result.body must beSome[GetSolutionsForQuestResult].which(_.solutions == List())
    }

    "Make correct db call in getSolutionsForUser" in context {
      db.solution.allWithParams(List(QuestSolutionStatus.OnVoting.toString), List("qid"), null, 10, null, null, null) returns List[QuestSolution]().iterator

      val result = api.getSolutionsForUser(GetSolutionsForUserRequest(User(), "qid", List(QuestSolutionStatus.OnVoting), 2, 5))

      there was one(solution).allWithParams(
        List(QuestSolutionStatus.OnVoting.toString),
        List("qid"),
        null,
        10,
        null,
        null,
        null,
        null)

      result.body must beSome[GetSolutionsForUserResult].which(_.solutions == List())
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

