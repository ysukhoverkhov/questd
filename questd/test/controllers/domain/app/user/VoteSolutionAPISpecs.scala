package controllers.domain.app.user

import controllers.domain.{BaseAPISpecs, OkApiResult}
import models.domain.solution.SolutionStatus
import testhelpers.domainstubs._

class VoteSolutionAPISpecs extends BaseAPISpecs {

  "Vote Solution API" should {
    "hideOwnSolution hides own solutions only" in context {
      val u = createUserStub()
      val s = createSolutionStub(authorId = u.id + "qe")

      db.solution.readById(any) returns Some(s)

      val result = api.hideOwnSolution(HideOwnSolutionRequest(u, s.id))

      result must beAnInstanceOf[OkApiResult[HideOwnSolutionResult]]
      result.body.get.allowed must beEqualTo(HideOwnSolutionCode.NotOwnSolution)
    }

    "hideOwnSolution report missing solutions correctly" in context {
      val u = createUserStub()
      val s = createSolutionStub(authorId = u.id)

      db.solution.readById(any) returns None

      val result = api.hideOwnSolution(HideOwnSolutionRequest(u, s.id))

      result must beAnInstanceOf[OkApiResult[HideOwnSolutionResult]]
      result.body.get.allowed must beEqualTo(HideOwnSolutionCode.SolutionNotFound)
    }

    "hideOwnSolution in rotation only" in context {
      val u = createUserStub()
      val s = createSolutionStub(authorId = u.id, status = SolutionStatus.AdminBanned)

      db.solution.readById(any) returns Some(s)

      val result = api.hideOwnSolution(HideOwnSolutionRequest(u, s.id))

      result must beAnInstanceOf[OkApiResult[HideOwnSolutionResult]]
      result.body.get.allowed must beEqualTo(HideOwnSolutionCode.SolutionNotInRotation)
    }

    "hideOwnSolution works" in context {
      val u = createUserStub()
      val s = createSolutionStub(authorId = u.id)

      db.solution.readById(any) returns Some(s)

      val result = api.hideOwnSolution(HideOwnSolutionRequest(u, s.id))

      result must beAnInstanceOf[OkApiResult[HideOwnSolutionResult]]
      result.body.get.allowed must beEqualTo(HideOwnSolutionCode.OK)
    }
  }
}
