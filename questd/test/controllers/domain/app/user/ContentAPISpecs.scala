package controllers.domain.app.user

import controllers.domain._
import models.domain._
import controllers.domain.app.quest._
import testhelpers.domainstubs._

class ContentAPISpecs extends BaseAPISpecs {

  "Content API" should {

    "Make correct db call in getSolutionsForQuest" in context {
      val u = createUserStub()

      db.solution.allWithParams(List(QuestSolutionStatus.Won.toString), null, null, 10, null, null, List("qid"), null) returns List[QuestSolution]().iterator

      val result = api.getSolutionsForQuest(GetSolutionsForQuestRequest(u, "qid", List(QuestSolutionStatus.Won), 2, 5))

      there was one(solution).allWithParams(
        List(QuestSolutionStatus.Won.toString),
        null,
        null,
        10,
        null,
        null,
        List("qid"),
        null,
        null)

      result.body must beSome[GetSolutionsForQuestResult].which(_.solutions == List())
    }

    "Make correct db call in getSolutionsForUser" in context {
      val u = createUserStub()

      db.solution.allWithParams(
        List(QuestSolutionStatus.Won.toString),
        List("qid"),
        null,
        10,
        null,
        null,
        null,
        null,
        null) returns List[QuestSolution]().iterator

      val result = api.getSolutionsForUser(GetSolutionsForUserRequest(u, "qid", List(QuestSolutionStatus.Won), 2, 5))

      there was one(solution).allWithParams(
        List(QuestSolutionStatus.Won.toString),
        List("qid"),
        null,
        10,
        null,
        null,
        null,
        null,
        null)

      result.body must beSome[GetSolutionsForUserResult].which(_.solutions == List())
    }

    // TODO: clean me up.
    // TODO: check - it looks like this test is already exists somewhere
//    "getLikedQuests calls db correctly" in context {
//      db.quest.allWithParams(List(QuestStatus.InRotation.toString), List(), Some(1, 2), 0, Some(false), List("1", "2", "3", "4")) returns List().iterator
//
//      val liked = List(
//        List("1", "2"),
//        List("3", "4"))
//      val u = createUserStub(likedQuestProposalIds = liked)
//      val result = api.getLikedQuests(GetLikedQuestsRequest(u, QuestStatus.InRotation, Some(1, 2)))
//
//      there was one(quest).allWithParams(
//        List(QuestStatus.InRotation.toString),
//        null,
//        Some(1, 2),
//        0,
//        null,
//        List("1", "2", "3", "4"),
//        u.demo.cultureId)
//    }

    "getVIPQuests calls db correctly" in context {

      db.quest.allWithParams(List(QuestStatus.InRotation.toString), List(), Some(1, 2), 0, Some(true), List()) returns List().iterator
      val u = createUserStub()

      val result = api.getVIPQuests(GetVIPQuestsRequest(u, QuestStatus.InRotation, Some(1, 2)))

      there was one(quest).allWithParams(
        List(QuestStatus.InRotation.toString),
        null,
        Some(1, 2),
        0,
        Some(true),
        null,
        u.demo.cultureId)
    }

    "getAllQuests calls db correctly" in context {

      db.quest.allWithParams(List(QuestStatus.InRotation.toString), List(), Some(1, 2), 0, None, List()) returns List().iterator

      val result = api.getAllQuests(GetAllQuestsRequest(createUserStub(cultureId = "cid"), QuestStatus.InRotation, Some(1, 2)))

      there was one(quest).allWithParams(
        List(QuestStatus.InRotation.toString),
        null,
        Some(1, 2),
        0,
        null,
        null,
        Some("cid"))
    }
  }
}

