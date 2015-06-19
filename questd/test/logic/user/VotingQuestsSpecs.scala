package logic.user

import controllers.domain.app.protocol.ProfileModificationResult
import logic.BaseLogicSpecs
import models.domain.common.ContentVote
import models.domain.user.profile.{Functionality, Rights}
import testhelpers.domainstubs._

class VotingQuestsSpecs extends BaseLogicSpecs {

  "User Logic for voting for quests" should {

    "Do not allow voting for quests without rights" in {
      applyConfigMock()

      val user = createUserStub(rights = Rights.none)
      val q = createQuestStub()

      val rv = user.canVoteQuest(q.id, ContentVote.Cool)

      rv must beEqualTo(ProfileModificationResult.NotEnoughRights)
    }

    "Do not allow cheating for quests without rights" in {
      applyConfigMock()

      val user = createUserStub(rights = Rights(unlockedFunctionality = Set(Functionality.VoteQuests)))
      val q = createQuestStub()

      val rv = user.canVoteQuest(q.id, ContentVote.Cheating)

      rv must beEqualTo(ProfileModificationResult.NotEnoughRights)
    }

    "Do allow voting for quests not in time line" in {
      applyConfigMock()

      val user = createUserStub()
      val q = createQuestStub()

      val rv = user.canVoteQuest(q.id, ContentVote.Cheating)

      rv must beEqualTo(ProfileModificationResult.OK)
    }

    "Do not allow voting for quests in time line but we already voted for" in {
      applyConfigMock()

      val q = createQuestStub()
      val user = createUserStub(
        timeLine = List(createTimeLineEntryStub(objectId = q.id)),
        votedQuests = Map(q.id -> ContentVote.Cool))

      val rv = user.canVoteQuest(q.id, ContentVote.Cheating)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do not allow voting for own quests" in {
      applyConfigMock()

      val userId = "userId"
      val q = createQuestStub()
      val user = createUserStub(
        id = userId,
        createdQuests = List(q.id))

      val rv = user.canVoteQuest(q.id, ContentVote.Cheating)

      rv must beEqualTo(ProfileModificationResult.OutOfContent)
    }

    "Allow voting for quests in normal situations" in {
      applyConfigMock()

      val q = createQuestStub()
      val user = createUserStub(timeLine = List(createTimeLineEntryStub(objectId = q.id)))

      val rv = user.canVoteQuest(q.id, ContentVote.Cool)

      rv must beEqualTo(ProfileModificationResult.OK)
    }
  }
}

