package controllers.domain.app.solution

import controllers.domain._
import models.domain.quest.QuestStatus
import models.domain.solution.SolutionStatus
import models.domain.user.friends.{FriendshipStatus, Friendship}
import models.domain.user.User
import models.domain.user.timeline.TimeLineType
import testhelpers.domainstubs._
import org.mockito.Matchers.{eq => mEq}

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
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = mEq(List(f1.id)),
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = mEq(None),
        ids = mEq(List.empty),
        idsExclude = mEq(List.empty),
        questIds = mEq(List.empty),
        themeIds = any,
        cultureId = mEq(u.demo.cultureId),
        withBattles = any) returns Iterator.empty
      db.solution.allWithParams(status = List(SolutionStatus.InRotation), authorIds = List(f1.id, f2.id), levels = Some((1, 2)), skip = 0, vip = None, ids = List.empty, questIds = List.empty, cultureId = u.demo.cultureId) returns List.empty.iterator

      val result = api.getFriendsSolutions(GetFriendsSolutionsRequest(
        user = u,
        status = List(SolutionStatus.InRotation),
        levels = Some((1, 2))))

      result must beAnInstanceOf[OkApiResult[GetFriendsSolutionsResult]]

      there was one(solution).allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = mEq(List(f1.id)),
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = mEq(List.empty),
        questIds = any,
        themeIds = any,
        cultureId = mEq(u.demo.cultureId),
        withBattles = any)

      there was no(solution).allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = mEq(List.empty),
        authorIdsExclude = any,
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = any,
        questIds = any,
        themeIds = any,
        cultureId = mEq(u.demo.cultureId),
        withBattles = any)

      there was no(solution).allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = mEq(List(f1.id, f2.id)),
        authorIdsExclude = any,
        levels = mEq(Some((1, 2))),
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = any,
        questIds = any,
        themeIds = any,
        cultureId = mEq(u.demo.cultureId),
        withBattles = any)
    }

    "getSolutionsForLikedQuests calls db correctly" in context {
      db.solution.allWithParams(
        status = List(SolutionStatus.InRotation),
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
        status = List(SolutionStatus.InRotation),
        levels = Some((1, 2))))

      result must beAnInstanceOf[OkApiResult[GetSolutionsForLikedQuestsResult]]
      there was one(solution).allWithParams(
        status = List(SolutionStatus.InRotation),
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
        status = List(SolutionStatus.InRotation),
        authorIds = List.empty,
        levels = Some((1, 2)),
        skip = 0,
        vip = Some(true),
        ids = List.empty,
        questIds = List("a")) returns List.empty.iterator

      val result = api.getVIPSolutions(GetVIPSolutionsRequest(
        user = User(),
        status = List(SolutionStatus.InRotation),
        levels = Some((1, 2)),
        themeIds = List("a")))

      result must beAnInstanceOf[OkApiResult[GetVIPSolutionsResult]]

      there was one(solution).allWithParams(
        status = List(SolutionStatus.InRotation),
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
        cultureId = any[Option[String]],
        withSolutions = any[Option[Boolean]]
      ) returns List(qu).iterator

      db.solution.allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = any,
        authorIdsExclude = mEq(List.empty),
        levels = mEq(None),
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = mEq(List.empty),
        questIds = mEq(List(qu.id)),
        themeIds = any,
        cultureId = mEq(None),
        withBattles = any) returns List(sol).iterator

      val result = api.getSolutionsForOwnQuests(GetSolutionsForOwnQuestsRequest(User(), List(SolutionStatus.InRotation)))

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
        cultureId = any[Option[String]],
        withSolutions = any[Option[Boolean]])

      there was one(solution).allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = any,
        authorIdsExclude = mEq(List.empty),
        levels = mEq(None),
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = mEq(List.empty),
        questIds = mEq(List(qu.id)),
        themeIds = any,
        cultureId = mEq(None),
        withBattles = any)
    }

    "getAllSolutions calls db correctly" in context {
      val u = createUserStub(cultureId = Some("cid"))

      db.solution.allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = any,
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = any,
        vip = any,
        ids = any,
        idsExclude = mEq(List.empty),
        questIds = any,
        themeIds = mEq(List("b")),
        cultureId = mEq(u.demo.cultureId),
        withBattles = any) returns Iterator.empty

      val result = api.getAllSolutions(GetAllSolutionsRequest(
        user = u,
        status = List(SolutionStatus.InRotation),
        levels = Some((1, 2)),
        themeIds = List("b"),
        cultureId = u.demo.cultureId))

      result must beAnInstanceOf[OkApiResult[GetAllSolutionsResult]]

      there was one(solution).allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = any,
        authorIdsExclude = mEq(List.empty),
        levels = mEq(Some((1, 2))),
        skip = any,
        vip = any,
        ids = any,
        idsExclude = mEq(List.empty),
        questIds = any,
        themeIds = mEq(List("b")),
        cultureId = mEq(u.demo.cultureId),
        withBattles = any)
    }
  }
}


