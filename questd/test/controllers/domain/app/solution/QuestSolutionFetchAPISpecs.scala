package controllers.domain.app.solution

import controllers.domain._
import models.domain._
import play.Logger
import testhelpers.domainstubs._

class QuestSolutionFetchAPISpecs extends BaseAPISpecs {

  "Quest solution Fetch API" should {

    "getFriendsSolutions return solutions for confirmed friends only" in context {

      def createUser(friends: List[Friendship]) = {
        User(friends = friends)
      }

      def createFriend(newid: String) = {
        User(id = newid)
      }

      val f1 = createFriend("f1")
      val f2 = createFriend("f2")

      val u = createUser(List(Friendship(f1.id, FriendshipStatus.Accepted), Friendship(f2.id, FriendshipStatus.Invited)))

      db.solution.allWithParams(List(SolutionStatus.OnVoting), List(f1.id), Some(1, 2), 0, None, List(), List(), cultureId = u.demo.cultureId) returns List().iterator
      db.solution.allWithParams(List(SolutionStatus.OnVoting), List(f1.id, f2.id), Some(1, 2), 0, None, List(), List(), cultureId = u.demo.cultureId) returns List().iterator

      val result = api.getFriendsSolutions(GetFriendsSolutionsRequest(u, List(SolutionStatus.OnVoting), Some(1, 2)))

      there was one(solution).allWithParams(
        List(SolutionStatus.OnVoting),
        List(f1.id),
        Some(1, 2),
        0,
        null,
        null,
        null,
        null,
        u.demo.cultureId)

      there was no(solution).allWithParams(
        List(SolutionStatus.OnVoting),
        List(),
        Some(1, 2),
        0,
        null,
        null,
        null,
        null,
        u.demo.cultureId)

      there was no(solution).allWithParams(
        List(SolutionStatus.OnVoting),
        List(f1.id, f2.id),
        Some(1, 2),
        0,
        null,
        null,
        null,
        null,
        u.demo.cultureId)
    }

    "getSolutionsForLikedQuests calls db correctly" in context {
      db.solution.allWithParams(List(SolutionStatus.OnVoting), List(), Some(1, 2), 0, Some(false), List("1", "2", "3", "4"), List()) returns List().iterator

      val liked = List("1", "2", "3", "4")
      val u = createUserStub(
        timeLine = liked.map(id => createTimeLineEntryStub(objectId = id, objectType = TimeLineType.Quest, ourVote = Some(ContentVote.Cool)))
      )
      val result = api.getSolutionsForLikedQuests(GetSolutionsForLikedQuestsRequest(u, List(SolutionStatus.OnVoting), Some(1, 2)))

      result must beAnInstanceOf[OkApiResult[GetSolutionsForLikedQuestsResult]]
      there was one(solution).allWithParams(
        List(SolutionStatus.OnVoting),
        null,
        Some(1, 2),
        0,
        null,
        null,
        List("1", "2", "3", "4"),
        null)
    }

    "getVIPSolutions calls db correctly" in context {

      db.solution.allWithParams(
        List(SolutionStatus.OnVoting),
        List(),
        Some(1, 2),
        0,
        Some(true),
        List(),
        List("a")) returns List().iterator

      val result = api.getVIPSolutions(GetVIPSolutionsRequest(User(), List(SolutionStatus.OnVoting), Some(1, 2), List("a")))

      there was one(solution).allWithParams(
        List(SolutionStatus.OnVoting),
        null,
        Some(1, 2),
        0,
        Some(true),
        null,
        null,
        List("a"))
    }

    "getHelpWantedSolutions calls db correctly with empty list" in context {

      val result = api.getHelpWantedSolutions(GetHelpWantedSolutionsRequest(User(), List(SolutionStatus.OnVoting)))

//      result must beEqualTo(OkApiResult(GetHelpWantedSolutionsResult(List().iterator)))

      there was no(solution).allWithParams(
        List(SolutionStatus.OnVoting),
        null,
        null,
        0,
        Some(true),
        null,
        null,
        List("a"))
    }

    "getHelpWantedSolutions calls db correctly with not empty list" in context {

      val sol = createSolutionStub()

      db.solution.allWithParams(
        List(SolutionStatus.OnVoting),
        null,
        None,
        0,
        null,
        List("solution_id"),
        null,
        null,
        None) returns List(sol).iterator

      val result = api.getHelpWantedSolutions(GetHelpWantedSolutionsRequest(User(mustVoteSolutions = List("solution_id")), List(SolutionStatus.OnVoting)))

      result.body.get.solutions.toList must beEqualTo(List(sol))

      there was one(solution).allWithParams(
        List(SolutionStatus.OnVoting),
        null,
        None,
        0,
        null,
        List("solution_id"),
        null,
        null,
        None)
    }

    "getSolutionsForOwnQuests calls db correctly" in context {

      val qu = createQuestStub()
      val sol = createSolutionStub()

      db.quest.allWithParams(
        status = any[List[QuestStatus.Value]],
        authorIds = any[List[String]],
        levels = any[Option[(Int, Int)]],
        skip = any[Int],
        vip = any[Option[Boolean]],
        ids = any[List[String]],
        cultureId = any[Option[String]]
      ) returns List(qu).iterator

      db.solution.allWithParams(
        List(SolutionStatus.OnVoting),
        null,
        None,
        0,
        null,
        null,
        List(qu.id),
        null,
        None) returns List(sol).iterator

      val result = api.getSolutionsForOwnQuests(GetSolutionsForOwnQuestsRequest(User(), List(SolutionStatus.OnVoting)))

      Logger.error(result.toString)

      result.body.get.solutions.toList must beEqualTo(List(sol))

      there was one(quest).allWithParams(
        status = any[List[QuestStatus.Value]],
        authorIds = any[List[String]],
        levels = any[Option[(Int, Int)]],
        skip = any[Int],
        vip = any[Option[Boolean]],
        ids = any[List[String]],
        cultureId = any[Option[String]])

      there was one(solution).allWithParams(
        List(SolutionStatus.OnVoting),
        null,
        None,
        0,
        null,
        null,
        List(qu.id),
        null,
        None)
    }

    "getAllSolutions calls db correctly" in context {
      val u = createUserStub(cultureId = "cid")

      db.solution.allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = List(),
        levels = Some(1, 2),
        skip = 0,
        vip = None,
        ids = List(),
        questIds = List("a"),
        themeIds = List("b"),
        cultureId = u.demo.cultureId) returns List().iterator

      val result = api.getAllSolutions(GetAllSolutionsRequest(u, List(SolutionStatus.OnVoting), Some(1, 2), List("b")))

      there was one(solution).allWithParams(
        List(SolutionStatus.OnVoting),
        null,
        Some(1, 2),
        0,
        null,
        null,
        null,
        List("b"),
        u.demo.cultureId)
    }
  }
}


