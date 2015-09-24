package controllers.domain.app.user

import controllers.domain._
import models.domain.solution.{Solution, SolutionStatus}
import testhelpers.domainstubs._
import org.mockito.Matchers.{eq => mEq}

class ContentAPISpecs extends BaseAPISpecs {

  "Content API" should {

    "Make correct db call in getSolutionsForQuest" in context {
      val banned = List("anned", "nabned")
      val u = createUserStub(banned = banned)

      db.solution.allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = any,
        authorIdsExclude = any,
        levels = any,
        skip = mEq(10),
        vip = any,
        ids = any,
        idsExclude = any,
        questIds = mEq(List("qid")),
        themeIds = any,
        cultureId = any,
        withBattles = any) returns List[Solution]().iterator

      val result = api.getSolutionsForQuest(GetSolutionsForQuestRequest(u, "qid", List(SolutionStatus.InRotation), 2, 5))

      there was one(solution).allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = any,
        authorIdsExclude = mEq(banned),
        levels = any,
        skip = mEq(10),
        vip = any,
        ids = any,
        idsExclude = any,
        questIds = mEq(List("qid")),
        themeIds = any,
        cultureId = any,
        withBattles = any)

      result.body must beSome[GetSolutionsForQuestResult].which(_.solutions == List.empty)
    }

    "Make correct db call in getSolutionsForUser" in context {
      val u = createUserStub()

      db.solution.allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = mEq(List("qid")),
        authorIdsExclude = any,
        levels = any,
        skip = mEq(10),
        vip = any,
        ids = any,
        idsExclude = any,
        questIds = any,
        themeIds = any,
        cultureId = any,
        withBattles = any) returns List[Solution]().iterator

      val result = api.getSolutionsForUser(GetSolutionsForUserRequest(u, "qid", List(SolutionStatus.InRotation), 2, 5))

      there was one(solution).allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = mEq(List("qid")),
        authorIdsExclude = any,
        levels = any,
        skip = mEq(10),
        vip = any,
        ids = any,
        idsExclude = any,
        questIds = any,
        themeIds = any,
        cultureId = any,
        withBattles = any)

      result.body must beSome[GetSolutionsForUserResult].which(_.solutions == List.empty)
    }
  }
}

