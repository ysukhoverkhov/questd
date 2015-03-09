package controllers.domain.app.user


import controllers.domain.{BaseAPISpecs, OkApiResult}
import controllers.domain.app.protocol.ProfileModificationResult
import models.domain._
import org.mockito.Matchers
import testhelpers.domainstubs._

class VoteSolutionAPISpecs extends BaseAPISpecs {

  "Vote Solution API" should {

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

    "Voting for friend's solution increase correct stats in solution" in context {

      val sid = "solution id"
      val friendId = "friendId"
      val s = createSolutionStub(id = sid, authorId = friendId, status = SolutionStatus.Won)
      val q = createQuestStub()
      val u = createUserStub(
        id = "uniqueid",
        timeLine = List(createTimeLineEntryStub(objectId = s.id)),
        friends = List(Friendship(friendId, FriendshipStatus.Accepted)))

      solution.readById(s.id) returns Some(s)

      solution.updatePoints(
        any,
        any,
        any,
        any,
        any,
        any,
        any,
        any) returns Some(s)
      user.recordSolutionVote(u.id, s.id, ContentVote.Cool) returns Some(u)

      val result = api.voteSolution(VoteSolutionRequest(
        user = u,
        solutionId = sid,
        vote = ContentVote.Cool))

      there was one(solution).updatePoints(
        any,
        any,
        any,
        Matchers.eq(1),
        Matchers.eq(1),
        any,
        any,
        any)

      result must beAnInstanceOf[OkApiResult[VoteSolutionResult]]
      result.body.get.allowed must beEqualTo(ProfileModificationResult.OK)
    }
  }
}
