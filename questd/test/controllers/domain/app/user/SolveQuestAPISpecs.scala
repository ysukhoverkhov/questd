package controllers.domain.app.user

import controllers.domain.{BaseAPISpecs, OkApiResult}
import controllers.domain.app.protocol.ProfileModificationResult
import models.domain._
import org.mockito.Matchers
import testhelpers.domainstubs._

class SolveQuestAPISpecs extends BaseAPISpecs {

  "Solve Quest API" should {

    "Create regular solution for regular users" in context {

      val uid = "uid"

      val q = createQuestStub()
      val tl = createTimeLineEntryStub(objectAuthorId = uid, objectId = q.id, objectType = TimeLineType.Quest)
      val t2 = createTimeLineEntryStub(objectType = TimeLineType.Solution, objectAuthorId = "uid", reason = TimeLineReason.Created)
      val t3 = createTimeLineEntryStub(objectType = TimeLineType.Quest)
      val t4 = createTimeLineEntryStub(objectType = TimeLineType.Quest)
      val friends = List(Friendship("fid1", FriendshipStatus.Accepted), Friendship("fid2", FriendshipStatus.Invited))
      val u = createUserStub(
        id = uid,
        cultureId = "cid",
        vip = true,
        timeLine = List(tl, t2, t3, t4),
        friends = friends,
        questBookmark = Some(q.id + "this is bookmark not for the quest"),
        privateDailyResults = List(createDailyResultStub(
          questsIncome = List(createQuestIncomeStub(questId = q.id)))))
      val author = createUserStub(
        id = q.info.authorId,
        privateDailyResults = List(createDailyResultStub(
          questsIncome = List(createQuestIncomeStub(questId = q.id)))))
      val s = createSolutionInfoContent

      quest.readById(Matchers.eq(q.id)) returns Some(q)
      quest.updatePoints(Matchers.eq(q.id), anyInt, anyInt, anyInt, anyInt, anyInt, anyInt) returns Some(q)
      user.recordQuestSolving(Matchers.eq(u.id), Matchers.eq(q.id), Matchers.eq(false)) returns Some(u)
      user.addEntryToTimeLine(Matchers.eq(u.id), any) returns Some(u)
      user.addToAssets(Matchers.eq(u.id), any) returns Some(u)
      user.readById(q.info.authorId) returns Some(author)
      user.storeQuestSolvingInDailyResult(Matchers.eq(q.info.authorId), any, any) returns Some(author)
      solution.allWithParams(
        status = any,
        authorIds = any,
        authorIdsExclude = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        idsExclude = any,
        questIds = any,
        themeIds = any,
        cultureId = any) returns List().iterator

      val result = api.solveQuest(SolveQuestRequest(u, q.id, s))

      there was one(solution).create(
        Solution(
          id = anyString,
          u.demo.cultureId.get,
          questLevel = q.info.level,
          info = SolutionInfo(
            content = s,
            authorId = u.id,
            questId = q.id,
            vip = true)))
      there was one(quest).readById(q.id)
      there was one(user).recordQuestSolving(Matchers.eq(u.id), Matchers.eq(q.id), Matchers.eq(false))
      there was one(user).addEntryToTimeLine(Matchers.eq(u.id), any)
      there was one(user).addToAssets(Matchers.eq(u.id), any)
      there was one(user).addEntryToTimeLineMulti(Matchers.eq(List("fid1")), any)
      there was one(quest).updatePoints(Matchers.eq(q.id), Matchers.eq(2), anyInt, anyInt, anyInt, anyInt, anyInt)
      there was one(user).storeQuestSolvingInDailyResult(Matchers.eq(q.info.authorId), any, any)

      result must beEqualTo(OkApiResult(SolveQuestResult(ProfileModificationResult.OK, Some(u.profile))))
    }

    // FIX: clean me up.
//    "Report not enough assets for poor user if he wants to invite friends" in context {
//      val u = createUserStub(assets = Assets(0, 0, 0))
//      val s = createSolutionInfoContent
//
//      val result = api.solveQuest(ProposeSolutionRequest(u, s, List("1", "2", "3")))
//
//      result must beEqualTo(OkApiResult(ProposeSolutionResult(ProfileModificationResult.NotEnoughAssets, None)))
//    }

    // FIX: clean me up.
//    "Do not store id of solution for help for not friends" in context {
//      val friendsIds = List("1", "2", "3")
//      val requestedFriendsIds = List("4")
//      val notFriends = List("5")
//
//      val u = createUserStub(
//        assets = Assets(30, 30, 30),
//        friends =
//          friendsIds.map(id => Friendship(friendId = id, status = FriendshipStatus.Accepted)) :::
//            requestedFriendsIds.map(id => Friendship(friendId = id, status = FriendshipStatus.Invited)))
//      val s = createSolutionInfoContent
//
//      user.resetQuestSolution(any, any) returns Some(u)
//      db.user.addToAssets(any, any) returns Some(u)
//
////      db.solution.create(solution)
////
////      db.user.resetQuestSolution(
////        user.id,
////        config(api.ConfigParams.DebugDisableSolutionCooldown) == "1") ifSome { u =>
//
////      db.user.populateMustVoteSolutionsList(
////        userIds = filteredFriends,
////        solutionId = request.solutionId)
//// db.user.addToAssets(user.id, del2)
//
//      val result = api.proposeSolution(ProposeSolutionRequest(u, s, friendsIds ::: requestedFriendsIds ::: notFriends))
//
//      result must beEqualTo(OkApiResult(ProposeSolutionResult(ProfileModificationResult.OK, Some(u.profile))))
//
//      there was one(solution).create(any)
//      there was one(user).resetQuestSolution(any, any)
//      there was one(user).populateMustVoteSolutionsList(Matchers.eq(friendsIds), any)
//      there was one(user).addToAssets(any, any)
//
//    }

  }
}
