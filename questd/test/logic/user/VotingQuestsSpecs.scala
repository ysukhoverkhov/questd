package logic.user

import controllers.domain.app.user.VoteQuestByUserCode
import logic.BaseLogicSpecs
import models.domain.common.ContentVote
import models.domain.user.profile.{Functionality, Rights}
import testhelpers.domainstubs._

class VotingQuestsSpecs extends BaseLogicSpecs {

  "User Logic for voting for quests" should {

    "Do not allow voting for quests without rights" in context {
      val user = createUserStub(rights = Rights.none)
      val q = createQuestStub()

      val rv = user.canVoteQuest(q.id, ContentVote.Cool)

      rv must beEqualTo(VoteQuestByUserCode.NotEnoughRights)
    }

    "Do not allow cheating for quests without rights" in context {
      val user = createUserStub(rights = Rights(unlockedFunctionality = Set(Functionality.VoteQuests), 10))
      val q = createQuestStub()

      val rv = user.canVoteQuest(q.id, ContentVote.Cheating)

      rv must beEqualTo(VoteQuestByUserCode.NotEnoughRights)
    }

    "Do allow voting for quests not in time line" in context {
      val user = createUserStub()
      val q = createQuestStub()

      val rv = user.canVoteQuest(q.id, ContentVote.Cheating)

      rv must beEqualTo(VoteQuestByUserCode.OK)
    }

    "Do not allow voting for quests in time line but we already voted for" in context {
      val q = createQuestStub()
      val user = createUserStub(
        timeLine = List(createTimeLineEntryStub(objectId = q.id)),
        votedQuests = Map(q.id -> ContentVote.Cool))

      val rv = user.canVoteQuest(q.id, ContentVote.Cheating)

      rv must beEqualTo(VoteQuestByUserCode.QuestAlreadyVoted)
    }

    "Do not allow voting for own quests" in context {
      val userId = "userId"
      val q = createQuestStub()
      val user = createUserStub(
        id = userId,
        createdQuests = List(q.id))

      val rv = user.canVoteQuest(q.id, ContentVote.Cheating)

      rv must beEqualTo(VoteQuestByUserCode.CantVoteOwnQuest)
    }

    "Allow voting for quests in normal situations" in context {
      val q = createQuestStub()
      val user = createUserStub(timeLine = List(createTimeLineEntryStub(objectId = q.id)))

      val rv = user.canVoteQuest(q.id, ContentVote.Cool)

      rv must beEqualTo(VoteQuestByUserCode.OK)
    }
  }
}

