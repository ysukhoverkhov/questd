package controllers.domain.app.user


import controllers.domain.{BaseAPISpecs, OkApiResult}
import controllers.domain.app.protocol.ProfileModificationResult
import models.domain._
import models.domain.view.{QuestInfoWithID, PublicProfileWithID, QuestSolutionInfoWithID}
import testhelpers.domainstubs._

class VoteSolutionAPISpecs extends BaseAPISpecs {

  "Vote Solution API" should {

    // TODO: clean me up.
//    "Remove solution from list of mustVoteSolutions if it's selected" in context {
//
//      val sid = "solution id"
//      val u = createUserStub(id = "uniqueid", mustVoteSolutions = List(sid))
//      val s = createSolutionStub(id = sid)
//      val q = createQuestStub()
//
//      db.solution.allWithParams(
//        any,
//        any,
//        any,
//        any,
//        any,
//        any,
//        any,
//        any,
//        any) returns List(s).iterator
//      db.user.readById(any) returns Some(createUserStub())
//      db.quest.readById(any) returns Some(createQuestStub())
//      db.user.selectQuestSolutionVote(
//        any,
//        any,
//        any,
//        any) returns Some(u)
//      db.user.removeMustVoteSolution(u.id, s.id)
//
//      val result = api.getQuestSolutionToVote(GetQuestSolutionToVoteRequest(u))
//
//      result must beEqualTo(OkApiResult(GetQuestSolutionToVoteResult(ProfileModificationResult.OK, Some(u.profile))))
//      there was one(user).removeMustVoteSolution(u.id, s.id)
//    }
  }
}
