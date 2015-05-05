package logic.user

import controllers.domain.app.protocol.ProfileModificationResult
import logic.BaseLogicSpecs
import models.domain.{ContentVote, Rights}
import testhelpers.domainstubs._

class VotingQuestsSpecs extends BaseLogicSpecs {

  "User Logic for voting for quests" should {

    "Do not allow voting for quests without rights" in {
      api.config returns createStubConfig

      val user = createUserStub(rights = Rights.none)
      val q = createQuestStub()

      val rv = user.canVoteQuest(q.id)

      rv must beEqualTo(ProfileModificationResult.NotEnoughRights)
    }

    "Do allow voting for quests not in time line" in {
      api.config returns createStubConfig

      val user = createUserStub()
      val q = createQuestStub()

      val rv = user.canVoteQuest(q.id)

      rv must beEqualTo(ProfileModificationResult.OK)
    }

    "Do not allow voting for quests in time line but we already voted for" in {
      api.config returns createStubConfig

      val q = createQuestStub()
      val user = createUserStub(
        timeLine = List(createTimeLineEntryStub(objectId = q.id)),
        votedQuests = Map(q.id -> ContentVote.Cool))

      val rv = user.canVoteQuest(q.id)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do not allow voting for own quests" in {
      api.config returns createStubConfig

      val userId = "userId"
      val q = createQuestStub()
      val user = createUserStub(
        id = userId,
        createdQuests = List(q.id))

      val rv = user.canVoteQuest(q.id)

      rv must beEqualTo(ProfileModificationResult.OutOfContent)
    }

    "Allow voting for quests in normal situations" in {
      api.config returns createStubConfig

      val q = createQuestStub()
      val user = createUserStub(timeLine = List(createTimeLineEntryStub(objectId = q.id)))

      val rv = user.canVoteQuest(q.id)

      rv must beEqualTo(ProfileModificationResult.OK)
    }
  }
}

