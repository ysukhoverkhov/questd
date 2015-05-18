package logic.user

import controllers.domain.app.protocol.ProfileModificationResult
import logic.BaseLogicSpecs
import models.domain.common.ContentVote
import models.domain.user.Rights
import testhelpers.domainstubs._

class VotingSolutionsSpecs extends BaseLogicSpecs {

  "User Logic for voting for solutions" should {

    "Do not allow voting for solutions without rights" in {
      api.config returns createStubConfig

      val user = createUserStub(rights = Rights.none)
      val s = createSolutionStub()

      val rv = user.canVoteSolution(s.id)

      rv must beEqualTo(ProfileModificationResult.NotEnoughRights)
    }

    "Do allow voting for solutions not in time line" in {
      api.config returns createStubConfig

      val user = createUserStub()
      val s = createSolutionStub()

      val rv = user.canVoteSolution(s.id)

      rv must beEqualTo(ProfileModificationResult.OK)
    }

    "Do not allow voting for solutions in time line but we already voted for" in {
      api.config returns createStubConfig

      val s = createSolutionStub()
      val user = createUserStub(
        timeLine = List(createTimeLineEntryStub(objectId = s.id)),
        votedSolutions = Map(s.id -> ContentVote.Cool))

      val rv = user.canVoteSolution(s.id)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do not allow voting for solutions created by us" in {
      api.config returns createStubConfig

      val uid = "uid"
      val s = createSolutionStub()
      val user = createUserStub(id = uid, createdSolutions = List(s.id))

      val rv = user.canVoteSolution(s.id)

      rv must beEqualTo(ProfileModificationResult.OutOfContent)
    }

    "Do not allow voting solutions with incomplete bio" in {
      api.config returns createStubConfig

      val s = createSolutionStub()
      val user = createUserStub(cultureId = None)

      val rv = user.canVoteSolution(s.id)

      rv must beEqualTo(ProfileModificationResult.IncompleteBio)
    }

    "Allow voting for solutions in normal situations" in {
      api.config returns createStubConfig

      val s = createSolutionStub()
      val user = createUserStub(timeLine = List(createTimeLineEntryStub(objectId = s.id)))

      val rv = user.canVoteSolution(s.id)

      rv must beEqualTo(ProfileModificationResult.OK)
    }
  }
}

