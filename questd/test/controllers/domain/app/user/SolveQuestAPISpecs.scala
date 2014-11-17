package controllers.domain.app.user

import java.util.Date

import controllers.domain.BaseAPISpecs
import controllers.domain.app.protocol.ProfileModificationResult
import models.domain._
import controllers.domain.OkApiResult
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
      val u = createUserStub(id = uid, cultureId = "cid", vip = true, timeLine = List(tl, t2, t3, t4), friends = friends, questBookmark = Some(q.id))
      val s = createSolutionInfoContent

      quest.readById(Matchers.eq(q.id)) returns Some(q)
      quest.updatePoints(Matchers.eq(q.id), anyInt, anyInt, anyInt, anyInt, anyInt) returns Some(q)
      user.recordQuestSolving(Matchers.eq(u.id), Matchers.eq(q.id)) returns Some(u)
      user.addEntryToTimeLine(Matchers.eq(u.id), any) returns Some(u)
      user.addToAssets(Matchers.eq(u.id), any) returns Some(u)

      val result = api.solveQuest(SolveQuestRequest(u, q.id, s))

      result must beEqualTo(OkApiResult(SolveQuestResult(ProfileModificationResult.OK, Some(u.profile))))

      there was one(solution).create(
        QuestSolution(
          id = anyString,
          u.demo.cultureId.get,
          questLevel = q.info.level,
          info = QuestSolutionInfo(
            content = s,
            authorId = u.id,
            questId = q.id,
            vip = true),
          voteEndDate = new Date()))
      there was one(quest).readById(q.id)
      there was one(user).recordQuestSolving(Matchers.eq(u.id), Matchers.eq(q.id))
      there was one(user).addEntryToTimeLine(Matchers.eq(u.id), any)
      there was one(user).addToAssets(Matchers.eq(u.id), any)
      there was one(user).addEntryToTimeLineMulti(Matchers.eq(List("fid1")), any)
      there was one(quest).updatePoints(Matchers.eq(q.id), Matchers.eq(2), anyInt, anyInt, anyInt, anyInt)
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

    "Do not fight with himself in quest" in context {

      val user1 = createUserStub(id = "user1")
      val mySolution = createSolutionStub(id = "solId1", authorId = user1.id, questId = "qid")

      solution.allWithParams(
        status = List(QuestSolutionStatus.WaitingForCompetitor),
        questIds = List(mySolution.info.questId)) returns List(mySolution).iterator

      val result = api.tryFightQuest(TryFightQuestRequest(mySolution))

      result must beEqualTo(OkApiResult(TryFightQuestResult()))

      there were no(solution).updateStatus(any, any, any)
      there were no(user).storeSolutionInDailyResult(any, any)
    }

    "Receive reward for winning quest battle" in context {

      val quest = createQuestStub("qid")
      val user1 = createUserStub(id = "user1")
      val mySolution = createSolutionStub(id = "solId1", authorId = user1.id, questId = quest.id, points = 1, status = QuestSolutionStatus.WaitingForCompetitor)
      val user2 = createUserStub(id = "user2")
      val rivalSolution = createSolutionStub(id = "solId2", authorId = user2.id, questId = quest.id, points = 0, status = QuestSolutionStatus.WaitingForCompetitor)

      solution.allWithParams(
        status = List(QuestSolutionStatus.WaitingForCompetitor),
        questIds = List(mySolution.info.questId)) returns List(mySolution, rivalSolution).iterator

      solution.updateStatus(mySolution.id, QuestSolutionStatus.Won, Some(rivalSolution.id)) returns Some(mySolution.copy(status = QuestSolutionStatus.Won))
      solution.updateStatus(rivalSolution.id, QuestSolutionStatus.Lost, Some(mySolution.id)) returns Some(rivalSolution.copy(status = QuestSolutionStatus.Lost))

      user.readById(mySolution.info.authorId) returns Some(user1)
      user.readById(rivalSolution.info.authorId) returns Some(user2)

      db.user.storeSolutionInDailyResult(Matchers.eq(user1.id), any) returns Some(user1)
      db.user.storeSolutionInDailyResult(Matchers.eq(user2.id), any) returns Some(user2)

      db.quest.readById(quest.id) returns Some(quest)

      val result = api.tryFightQuest(TryFightQuestRequest(mySolution))

      result must beEqualTo(OkApiResult(TryFightQuestResult()))

      there was
        one(solution).updateStatus(mySolution.id, QuestSolutionStatus.Won, Some(rivalSolution.id)) andThen
        one(solution).updateStatus(rivalSolution.id, QuestSolutionStatus.Lost, Some(mySolution.id))
      there were two(user).readById(any)
      there were two(user).storeSolutionInDailyResult(any, any)
    }

    "Nominate both as winners in case of equal points" in context {

      val quest = createQuestStub("qid")
      val user1 = createUserStub(id = "user1")
      val mySolution = createSolutionStub(id = "solId1", authorId = user1.id, questId = quest.id, points = 5, status = QuestSolutionStatus.WaitingForCompetitor)
      val user2 = createUserStub(id = "user2")
      val rivalSolution = createSolutionStub(id = "solId2", authorId = user2.id, questId = quest.id, points = 5, status = QuestSolutionStatus.WaitingForCompetitor)

      solution.allWithParams(
        status = List(QuestSolutionStatus.WaitingForCompetitor),
        questIds = List(mySolution.info.questId)) returns List(mySolution, rivalSolution).iterator

      solution.updateStatus(mySolution.id, QuestSolutionStatus.Won, Some(rivalSolution.id)) returns Some(mySolution.copy(status = QuestSolutionStatus.Won))
      solution.updateStatus(rivalSolution.id, QuestSolutionStatus.Won, Some(mySolution.id)) returns Some(rivalSolution.copy(status = QuestSolutionStatus.Won))

      user.readById(mySolution.info.authorId) returns Some(user1)
      user.readById(rivalSolution.info.authorId) returns Some(user2)

      db.user.storeSolutionInDailyResult(Matchers.eq(user1.id), any) returns Some(user1)
      db.user.storeSolutionInDailyResult(Matchers.eq(user2.id), any) returns Some(user2)

      db.quest.readById(quest.id) returns Some(quest)

      val result = api.tryFightQuest(TryFightQuestRequest(mySolution))

      result must beEqualTo(OkApiResult(TryFightQuestResult()))

      there was
        one(solution).updateStatus(mySolution.id, QuestSolutionStatus.Won, Some(rivalSolution.id)) andThen
        one(solution).updateStatus(rivalSolution.id, QuestSolutionStatus.Won, Some(mySolution.id))
      there were two(user).readById(any)
      there were two(user).storeSolutionInDailyResult(any, any)
    }
  }
}
