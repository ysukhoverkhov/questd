package controllers.domain.app.user

import controllers.domain._
import models.domain._
import testhelpers.domainstubs._

class ContentAPISpecs extends BaseAPISpecs {

  "Content API" should {

    "Make correct db call in getSolutionsForQuest" in context {
      val u = createUserStub()

      db.solution.allWithParams(List(QuestSolutionStatus.Won), null, null, 10, null, null, List("qid"), null) returns List[QuestSolution]().iterator

      val result = api.getSolutionsForQuest(GetSolutionsForQuestRequest(u, "qid", List(QuestSolutionStatus.Won), 2, 5))

      there was one(solution).allWithParams(
        List(QuestSolutionStatus.Won),
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
        List(QuestSolutionStatus.Won),
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
        List(QuestSolutionStatus.Won),
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
  }
}

