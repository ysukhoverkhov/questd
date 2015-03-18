package controllers.domain.app.solution

import controllers.domain._
import models.domain._
import testhelpers.domainstubs._

class SolutionFetchAPISpecs extends BaseAPISpecs {

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

      db.solution.allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = List(f1.id),
        authorIdsExclude = List.empty,
        levels = Some((1, 2)),
        skip = 0,
        vip = None,
        ids = List.empty,
        idsExclude = List.empty,
        questIds = List.empty,
        cultureId = u.demo.cultureId) returns List.empty.iterator
      db.solution.allWithParams(status = List(SolutionStatus.OnVoting), authorIds = List(f1.id, f2.id), levels = Some((1, 2)), skip = 0, vip = None, ids = List.empty, questIds = List.empty, cultureId = u.demo.cultureId) returns List.empty.iterator

      val result = api.getFriendsSolutions(GetFriendsSolutionsRequest(
        user = u,
        status = List(SolutionStatus.OnVoting),
        levels = Some((1, 2))))

      there was one(solution).allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = List(f1.id),
        authorIdsExclude = List.empty,
        levels = Some((1, 2)),
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = List.empty,
        questIds = null,
        themeIds = null,
        cultureId = u.demo.cultureId)

      there was no(solution).allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = List.empty,
        authorIdsExclude = null,
        levels = Some((1, 2)),
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = null,
        questIds = null,
        themeIds = null,
        cultureId = u.demo.cultureId)

      there was no(solution).allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = List(f1.id, f2.id),
        authorIdsExclude = null,
        levels = Some((1, 2)),
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = null,
        questIds = null,
        themeIds = null,
        cultureId = u.demo.cultureId)
    }

    "getSolutionsForLikedQuests calls db correctly" in context {
      db.solution.allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = List.empty,
        levels = Some((1, 2)),
        skip = 0,
        vip = Some(false),
        ids = List("1", "2", "3", "4"),
        questIds = List.empty) returns List.empty.iterator

      val liked = List("1", "2", "3", "4")
      val u = createUserStub(
        timeLine = liked.map(id => createTimeLineEntryStub(objectId = id, objectType = TimeLineType.Quest))
      )
      val result = api.getSolutionsForLikedQuests(GetSolutionsForLikedQuestsRequest(
        user = u,
        status = List(SolutionStatus.OnVoting),
        levels = Some((1, 2))))

      result must beAnInstanceOf[OkApiResult[GetSolutionsForLikedQuestsResult]]
      there was one(solution).allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = null,
        authorIdsExclude = null,
        levels = Some((1, 2)),
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = null,
        questIds = List("1", "2", "3", "4"),
        themeIds = null)
    }

    "getVIPSolutions calls db correctly" in context {

      db.solution.allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = List.empty,
        levels = Some((1, 2)),
        skip = 0,
        vip = Some(true),
        ids = List.empty,
        questIds = List("a")) returns List.empty.iterator

      val result = api.getVIPSolutions(GetVIPSolutionsRequest(
        user = User(),
        status = List(SolutionStatus.OnVoting),
        levels = Some((1, 2)),
        themeIds = List("a")))

      there was one(solution).allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = null,
        authorIdsExclude = null,
        levels = Some((1, 2)),
        skip = 0,
        vip = Some(true),
        ids = null,
        idsExclude = null,
        questIds = null,
        themeIds = List("a"))
    }

    "getHelpWantedSolutions calls db correctly with empty list" in context {

      val result = api.getHelpWantedSolutions(GetHelpWantedSolutionsRequest(User(), List(SolutionStatus.OnVoting)))

//      result must beEqualTo(OkApiResult(GetHelpWantedSolutionsResult(List.empty.iterator)))

      there was no(solution).allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = null,
        levels = null,
        skip = 0,
        vip = Some(true),
        ids = null,
        questIds = null,
        themeIds = List("a"))
    }

    "getHelpWantedSolutions calls db correctly with not empty list" in context {

      val sol = createSolutionStub()

      db.solution.allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = null,
        authorIdsExclude = List.empty,
        levels = None,
        skip = 0,
        vip = null,
        ids = List("solution_id"),
        idsExclude = List.empty,
        questIds = null,
        themeIds = null,
        cultureId = None) returns List(sol).iterator

      val result = api.getHelpWantedSolutions(GetHelpWantedSolutionsRequest(User(mustVoteSolutions = List("solution_id")), List(SolutionStatus.OnVoting)))

      result.body.get.solutions.toList must beEqualTo(List(sol))

      there was one(solution).allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = null,
        authorIdsExclude = List.empty,
        levels = None,
        skip = 0,
        vip = null,
        ids = List("solution_id"),
        idsExclude = List.empty,
        questIds = null,
        themeIds = null,
        cultureId = None)
    }

    "getSolutionsForOwnQuests calls db correctly" in context {

      val qu = createQuestStub()
      val sol = createSolutionStub()

      db.quest.allWithParams(
        status = any[List[QuestStatus.Value]],
        authorIds = any[List[String]],
        authorIdsExclude = any[List[String]],
        levels = any[Option[(Int, Int)]],
        skip = any[Int],
        vip = any[Option[Boolean]],
        ids = any[List[String]],
        idsExclude = any[List[String]],
        cultureId = any[Option[String]]
      ) returns List(qu).iterator

      db.solution.allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = null,
        authorIdsExclude = List.empty,
        levels = None,
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = List.empty,
        questIds = List(qu.id),
        themeIds = null,
        cultureId = None) returns List(sol).iterator

      val result = api.getSolutionsForOwnQuests(GetSolutionsForOwnQuestsRequest(User(), List(SolutionStatus.OnVoting)))

      result.body.get.solutions.toList must beEqualTo(List(sol))

      there was one(quest).allWithParams(
        status = any[List[QuestStatus.Value]],
        authorIds = any[List[String]],
        authorIdsExclude = any[List[String]],
        levels = any[Option[(Int, Int)]],
        skip = any[Int],
        vip = any[Option[Boolean]],
        ids = any[List[String]],
        idsExclude = any[List[String]],
        cultureId = any[Option[String]])

      there was one(solution).allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = null,
        authorIdsExclude = List.empty,
        levels = None,
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = List.empty,
        questIds = List(qu.id),
        themeIds = null,
        cultureId = None)
    }

    "getAllSolutions calls db correctly" in context {
      val u = createUserStub(cultureId = Some("cid"))

      db.solution.allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = List.empty,
        levels = Some((1, 2)),
        skip = 0,
        vip = None,
        ids = List.empty,
        questIds = List("a"),
        themeIds = List("b"),
        cultureId = u.demo.cultureId) returns List.empty.iterator

      val result = api.getAllSolutions(GetAllSolutionsRequest(
        user = u,
        status = List(SolutionStatus.OnVoting),
        levels = Some((1, 2)),
        themeIds = List("b")))

      there was one(solution).allWithParams(
        status = List(SolutionStatus.OnVoting),
        authorIds = null,
        authorIdsExclude = List.empty,
        levels = Some((1, 2)),
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = List.empty,
        questIds = null,
        themeIds = List("b"),
        cultureId = u.demo.cultureId)
    }
  }
}


