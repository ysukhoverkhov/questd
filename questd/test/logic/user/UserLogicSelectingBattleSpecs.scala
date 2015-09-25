package logic.user

import controllers.domain.OkApiResult
import controllers.domain.app.battle._
import logic.BaseLogicSpecs
import testhelpers.domainstubs._

class UserLogicSelectingBattleSpecs extends BaseLogicSpecs {

  "User Logic" should {

    "Return battle from friends if dice rolls so" in {

      applyConfigMock()
      rand.nextDouble returns 0.13

      val bid = "qid"

      api.getFriendsBattles(any[GetFriendsBattlesRequest]) returns OkApiResult(GetFriendsBattlesResult(List(createBattleStub(bid)).iterator))

      val u = createUserStub()
      val q = u.getRandomBattlesForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getFriendsBattles(any[GetFriendsBattlesRequest])

      q.map(_.id) must beEqualTo(List(bid))
    }

    "Return battles from following if dice rolls so" in {
      applyConfigMock()
      rand.nextDouble returns 0.38

      val bid = "qid"

      api.getFollowingBattles(any[GetFollowingBattlesRequest]) returns OkApiResult(GetFollowingBattlesResult(List(createBattleStub(bid)).iterator))

      val u = createUserStub()
      val q = u.getRandomBattlesForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getFollowingBattles(any[GetFollowingBattlesRequest])

      q.map(_.id) must beEqualTo(List(bid))
    }

//    "Return liked quest if dice rolls so for solving" in {
//      api.config returns createStubConfig
//      rand.nextDouble returns 0.58
//
//      val qid = "qid"
//
//      api.getLikedQuests(any[GetLikedQuestsRequest]) returns OkApiResult(GetLikedQuestsResult(List(createQuestStub(qid, "author")).iterator))
//
//      val u = User()
//      val q = u.getRandomQuestsForTimeLine(1)
//
//      there was one(rand).nextDouble
//      there was one(api).getLikedQuests(any[GetLikedQuestsRequest])
//
//      q.map(_.id) must beEqualTo(List(qid))
//    }

//    "Do not return liked quest if dice rolls so for voting" in {
//      api.config returns createStubConfig
//      rand.nextDouble returns 0.98
//
//      val qid = "qid"
//
//      api.getAllQuests(any[GetAllQuestsRequest]) returns OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator))
//
//      val u = User()
//      val q = u.getRandomQuestsForTimeLine(1)
//
//      there was one(rand).nextDouble
//      there was no(api).getLikedQuests(any[GetLikedQuestsRequest])
//      there was one(api).getAllQuests(any[GetAllQuestsRequest])
//
//      q.map(_.id) must beEqualTo(List(qid))
//    }

    "Return VIP battles if dice rolls so" in {
      applyConfigMock()
      rand.nextDouble returns 0.75

      val bid = "qid"

      api.getVIPBattles(any[GetVIPBattlesRequest]) returns OkApiResult(GetVIPBattlesResult(List(createBattleStub(bid)).iterator))

      val u = createUserStub()
      val q = u.getRandomBattlesForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getVIPBattles(any[GetVIPBattlesRequest])

      q.map(_.id) must beEqualTo(List(bid))
    }

    // TAGS: implement me when tags will be impelemented.
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

    // TAGS: uncomment with tags.
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

//    "Starting quests return vip quests" in {
//      val qid = "qid"
//      val u = User(
//        profile = Profile(
//          publicProfile = PublicProfile(level = 1)))
//
//      api.config returns createStubConfig
//      rand.nextDouble returns 0.0
//
//      api.getVIPQuests(any[GetVIPQuestsRequest]) returns OkApiResult(GetVIPQuestsResult(List(createQuestStub(qid, "author")).iterator))
//
//      u.getRandomQuestsForTimeLine(1)
//
//      there was one(rand).nextDouble
//      there was one(api).getVIPQuests(any[GetVIPQuestsRequest])
//    }

//    "Starting quests return other quests" in {
//      val qid = "qid"
//      val u = User(
//        profile = Profile(
//          publicProfile = PublicProfile(level = 1)))
//
//      api.config returns createStubConfig
//      rand.nextDouble returns 1.0
//
//      api.getAllQuests(any[GetAllQuestsRequest]) returns OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator))
//
//      u.getRandomQuestsForTimeLine(1)
//
//      there was one(rand).nextDouble
//      there was one(api).getAllQuests(any[GetAllQuestsRequest])
//    }

//    "Starting quests does not return other quests ignoring recent quests list if no quests available otherwise" in {
//      val qid = "qid"
//      val u = createUserStub(level = 1, timeLine = List(createTimeLineEntryStub(objectId = qid)))
//
//      api.config returns createStubConfig
//      rand.nextDouble returns 1.0 thenReturns 1.0
//
//      api.getAllQuests(any[GetAllQuestsRequest]) returns
//        OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator)) thenReturns
//        OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator)) thenReturns
//        OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator)) thenReturns
//        OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator))
//
//      val q = u.getRandomQuestsForTimeLine(1)
//
//      there were one(rand).nextDouble
//      there were atLeast(1)(api).getAllQuests(any[GetAllQuestsRequest])
//      q.length must beEqualTo(1)
//    }

//    "Other quests are used if vip quests are unavailable" in {
//      val qid = "qid"
//      val u = User()
//
//      api.config returns createStubConfig
//      rand.nextDouble returns 0.75
//
//      api.getVIPQuests(any[GetVIPQuestsRequest]) returns OkApiResult(GetVIPQuestsResult(List.empty.iterator))
//      api.getAllQuests(any[GetAllQuestsRequest]) returns OkApiResult(GetAllQuestsResult(List(createQuestStub(qid, "author")).iterator))
//
//      u.getRandomQuestsForTimeLine(1)
//
//      there was one(rand).nextDouble
//      there was one(api).getVIPQuests(any[GetVIPQuestsRequest])
//      there was one(api).getAllQuests(any[GetAllQuestsRequest])
//    }

    "All battles are used if vip and Other battles are unavailable" in {
      val qid = "qid"
      val u = createUserStub()

      applyConfigMock()
      rand.nextDouble returns 0.75

      api.getVIPBattles(any[GetVIPBattlesRequest]) returns OkApiResult(GetVIPBattlesResult(List.empty.iterator))
      api.getAllBattles(any[GetAllBattlesRequest]) returns OkApiResult(GetAllBattlesResult(List.empty.iterator)) thenReturns OkApiResult(GetAllBattlesResult(List(createBattleStub()).iterator))

      u.getRandomBattlesForTimeLine(1)

      there was one(rand).nextDouble
      there was one(api).getVIPBattles(any[GetVIPBattlesRequest])
      there were two(api).getAllBattles(any[GetAllBattlesRequest])
    }
  }
}

