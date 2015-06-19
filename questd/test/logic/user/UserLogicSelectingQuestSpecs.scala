package logic.user

import controllers.domain.OkApiResult
import controllers.domain.app.quest._
import logic.BaseLogicSpecs
import models.domain.common.ContentVote
import models.domain.user.friends.Friendship
import models.domain.user.profile.{PublicProfile, Profile}
import models.domain.user.User
import testhelpers.domainstubs._

class UserLogicSelectingQuestSpecs extends BaseLogicSpecs {

  /**
   * Creates user we will test algorithm with
   */
  private def createUser(friends: List[Friendship]) = {
    User(friends = friends)
  }

  private def createFriend(newid: String) = {
    User(id = newid)
  }

  "User Logic" should {

    "Return quest from friends if dice rolls so" in {

      applyConfigMock()
      rand.nextDouble returns 0.13

      val qid = "qid"

      api.getFriendsQuests(any[GetFriendsQuestsRequest]) returns OkApiResult(GetFriendsQuestsResult(List(createQuestStub(qid, "author")).iterator))

      val u = createUserStub()
      val q = u.getRandomQuestsForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getFriendsQuests(any[GetFriendsQuestsRequest])

      q.map(_.id) must beEqualTo(List(qid))
    }

    "Return quest from following if dice rolls so" in {
      applyConfigMock()
      rand.nextDouble returns 0.38

      val qid = "qid"

      api.getFollowingQuests(any[GetFollowingQuestsRequest]) returns OkApiResult(GetFollowingQuestsResult(List(createQuestStub(qid, "author")).iterator))

      val u = createUserStub()
      val q = u.getRandomQuestsForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getFollowingQuests(any[GetFollowingQuestsRequest])

      q.map(_.id) must beEqualTo(List(qid))
    }

    "Do not return liked quest if dice rolls so for voting" in {
      applyConfigMock()
      rand.nextDouble returns 0.98

      val qid = "qid"

      api.getAllQuests(any[GetAllQuestsRequest]) returns OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator))

      val u = User()
      val q = u.getRandomQuestsForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getAllQuests(any[GetAllQuestsRequest])

      q.map(_.id) must beEqualTo(List(qid))
    }

    "Return VIP quest if dice rolls so" in {
      applyConfigMock()
      rand.nextDouble returns 0.75

      val qid = "qid"

      api.getVIPQuests(any[GetVIPQuestsRequest]) returns OkApiResult(GetVIPQuestsResult(List(createQuestStub(qid, "author")).iterator))

      val u = createUserStub()
      val q = u.getRandomQuestsForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getVIPQuests(any[GetVIPQuestsRequest])

      q.map(_.id) must beEqualTo(List(qid))
    }

    // TODO: implement me when tags will be impelemented.
//    "Return VIP quest with favorite theme ids if dice rolls so" in {
//      val qid = "qid"
//      val u = User(
//        profile = Profile(
//          publicProfile = PublicProfile(level = 10)),
//        history = UserHistory(
//          themesOfSelectedQuests = List("1", "2", "3", "4")))
//
//      api.config returns createStubConfig
//      rand.nextDouble returns 0.75
//      rand.nextInt(4) returns 0 thenReturns 1 thenReturns 2
//
//      api.getVIPQuests(GetVIPQuestsRequest(u, QuestStatus.InRotation, Some((-10, 11)), List("1", "2", "3"))) returns OkApiResult(GetVIPQuestsResult(List(createQuestStub(qid, "author")).iterator))
//
//      val q = u.getRandomQuestForTimeLine
//
//      there was one(rand).nextDouble
//      there were three(rand).nextInt(4)
//      there was one(api).getVIPQuests(GetVIPQuestsRequest(u, QuestStatus.InRotation, Some((-10, 11)), List("1", "2", "3")))
//
//      q must beSome.which(q => q.id == qid)
//    }

    // TODO: uncomment with tags.
//    "Return All quest with favorite theme ids if dice rolls so" in {
//      val qid = "qid"
//      val u = User(
//        history = UserHistory(
//          themesOfSelectedQuests = List("1", "2", "3", "4")))
//
//      api.config returns createStubConfig
//      rand.nextDouble returns 0.95
//      rand.nextInt(4) returns 1
//
//      api.getAllQuests(GetAllQuestsRequest(
//        u,
//        QuestStatus.InRotation,
//        Some((-2, 19)),
//        List("2"))) returns OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator))
//
//      val q = u.getRandomQuestForSolution
//
//      there was one(rand).nextDouble
//      there was one(rand).nextInt(4)
//      there was one(api).getAllQuests(GetAllQuestsRequest(u, QuestStatus.InRotation, Some((-2, 19)), List("2")))
//
//      q must beSome.which(q => q.id == qid)
//    }

    "Starting quests return vip quests" in {
      val qid = "qid"
      val u = User(
        profile = Profile(
          publicProfile = PublicProfile(level = 1)))

      applyConfigMock()
      rand.nextDouble returns 0.0

      api.getVIPQuests(any[GetVIPQuestsRequest]) returns OkApiResult(GetVIPQuestsResult(List(createQuestStub(qid, "author")).iterator))

      u.getRandomQuestsForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getVIPQuests(any[GetVIPQuestsRequest])
    }

    "Starting quests return other quests" in {
      val qid = "qid"
      val u = User(
        profile = Profile(
          publicProfile = PublicProfile(level = 1)))

      applyConfigMock()
      rand.nextDouble returns 1.0

      api.getAllQuests(any[GetAllQuestsRequest]) returns OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator))

      u.getRandomQuestsForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getAllQuests(any[GetAllQuestsRequest])
    }

    "Starting quests does not return other quests ignoring recent quests list if no quests available otherwise" in {
      val qid = "qid"
      val u = createUserStub(level = 1, timeLine = List(createTimeLineEntryStub(objectId = qid)))

      applyConfigMock()
      rand.nextDouble returns 1.0 thenReturns 1.0

      api.getAllQuests(any[GetAllQuestsRequest]) returns
        OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator)) thenReturns
        OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator)) thenReturns
        OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator)) thenReturns
        OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator))

      val q = u.getRandomQuestsForTimeLine(1)

      there were one(rand).nextDouble
      there were atLeast(1)(api).getAllQuests(any[GetAllQuestsRequest])
      q.length must beEqualTo(1)
    }

    "Starting quests does not return other quests ignoring voted quests quests list if no quests available otherwise" in {
      val qid = "qid"
      val u = createUserStub(level = 1, votedQuests = Map(qid -> ContentVote.Cheating))

      applyConfigMock()
      rand.nextDouble returns 1.0 thenReturns 1.0

      api.getAllQuests(any[GetAllQuestsRequest]) returns
        OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator)) thenReturns
        OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator)) thenReturns
        OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator)) thenReturns
        OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator))

      val q = u.getRandomQuestsForTimeLine(1)

      there were one(rand).nextDouble
      there were atLeast(1)(api).getAllQuests(any[GetAllQuestsRequest])
      q.length must beEqualTo(1)
    }

    "Other quests are used if vip quests are unavailable" in {
      val qid = "qid"
      val u = createUserStub()

      applyConfigMock()
      rand.nextDouble returns 0.75

      api.getVIPQuests(any[GetVIPQuestsRequest]) returns OkApiResult(GetVIPQuestsResult(List.empty.iterator))
      api.getAllQuests(any[GetAllQuestsRequest]) returns OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator))

      u.getRandomQuestsForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getVIPQuests(any[GetVIPQuestsRequest])
      there was one(api).getAllQuests(any[GetAllQuestsRequest])
    }

    "All quests are used if vip and Other quests are unavailable" in {
      val qid = "qid"
      val u = createUserStub()

      applyConfigMock()
      rand.nextDouble returns 0.75

      api.getVIPQuests(any[GetVIPQuestsRequest]) returns OkApiResult(GetVIPQuestsResult(List.empty.iterator))
      api.getAllQuests(any[GetAllQuestsRequest]) returns OkApiResult(GetAllQuestsResult(List.empty.iterator)) thenReturns OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator))

      u.getRandomQuestsForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getVIPQuests(any[GetVIPQuestsRequest])
      there were two(api).getAllQuests(any[GetAllQuestsRequest])
    }

    "All quests ignoring culture are used if vip and Other quests are unavailable" in {
      val qid = "qid"
      val u = createUserStub()

      applyConfigMock()
      rand.nextDouble returns 0.75

      api.getVIPQuests(any[GetVIPQuestsRequest]) returns OkApiResult(GetVIPQuestsResult(List.empty.iterator))
      api.getAllQuests(any[GetAllQuestsRequest]) returns OkApiResult(GetAllQuestsResult(List.empty.iterator)) thenReturns OkApiResult(GetAllQuestsResult(List.empty.iterator)) thenReturns OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator))

      u.getRandomQuestsForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getVIPQuests(any[GetVIPQuestsRequest])
      there were three(api).getAllQuests(any[GetAllQuestsRequest])
    }
  }
}

