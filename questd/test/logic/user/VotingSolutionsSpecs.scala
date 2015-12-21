package logic.user

import controllers.domain.app.user.VoteSolutionByUserCode
import logic.BaseLogicSpecs
import models.domain.common.ContentVote
import models.domain.user.profile.{Functionality, Rights}
import testhelpers.domainstubs._

class VotingSolutionsSpecs extends BaseLogicSpecs {

  "User Logic for voting for solutions" should {

    "Do not allow voting for solutions without rights" in context {
      val user = createUserStub(rights = Rights.none)
      val s = createSolutionStub()

      val rv = user.canVoteSolution(s.id, ContentVote.Cool)

      rv must beEqualTo(VoteSolutionByUserCode.NotEnoughRights)
    }

    "Do not allow cheating for solutions without rights" in context {
      val user = createUserStub(rights = Rights(unlockedFunctionality = Set(Functionality.VoteSolutions), 10))
      val s = createSolutionStub()

      val rv = user.canVoteSolution(s.id, ContentVote.Cheating)

      rv must beEqualTo(VoteSolutionByUserCode.NotEnoughRights)
    }

    "Do allow cheating for solutions without rights" in context {
      val user = createUserStub(rights = Rights(unlockedFunctionality = Set(Functionality.VoteSolutions, Functionality.Report), 10))
      val s = createSolutionStub()

      val rv = user.canVoteSolution(s.id, ContentVote.Cheating)

      rv must beEqualTo(VoteSolutionByUserCode.OK)
    }

    "Do allow voting for solutions not in time line" in context {
      val user = createUserStub()
      val s = createSolutionStub()

      val rv = user.canVoteSolution(s.id, ContentVote.Cool)

      rv must beEqualTo(VoteSolutionByUserCode.OK)
    }

    "Do not allow voting for solutions in time line but we already voted for" in context {
      val s = createSolutionStub()
      val user = createUserStub(
        timeLine = List(createTimeLineEntryStub(objectId = s.id)),
        votedSolutions = Map(s.id -> ContentVote.Cool))

      val rv = user.canVoteSolution(s.id, ContentVote.Cool)

      rv must beEqualTo(VoteSolutionByUserCode.SolutionAlreadyVoted)
    }

    "Do not allow voting for solutions created by us" in context {
      val uid = "uid"
      val s = createSolutionStub()
      val user = createUserStub(id = uid, solvedQuests = Map("q" -> s.id))

      val rv = user.canVoteSolution(s.id, ContentVote.Cool)

      rv must beEqualTo(VoteSolutionByUserCode.CantVoteOwnSolution)
    }

    "Do not allow voting solutions with incomplete bio" in context {
      val s = createSolutionStub()
      val user = createUserStub(cultureId = None)

      val rv = user.canVoteSolution(s.id, ContentVote.Cool)

      rv must beEqualTo(VoteSolutionByUserCode.IncompleteBio)
    }

    "Allow voting for solutions in normal situations" in context {
      val s = createSolutionStub()
      val user = createUserStub(timeLine = List(createTimeLineEntryStub(objectId = s.id)))

      val rv = user.canVoteSolution(s.id, ContentVote.Cool)

      rv must beEqualTo(VoteSolutionByUserCode.OK)
    }
  }
}

