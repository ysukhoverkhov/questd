package controllers.domain.app.battle

import controllers.domain._
import models.domain.battle.BattleStatus
import models.domain.common.ContentVote
import models.domain.user.friends.{Friendship, FriendshipStatus}
import org.mockito.Matchers.{eq => mEq}
import testhelpers.domainstubs._

class BattleFetchAPISpecs extends BaseAPISpecs {

  "Battle Fetch API" should {
    "getFriendsBattles return battles for confirmed friends only" in context {
      val f1 = createUserStub(id = "f1")
      val f2 = createUserStub(id = "f2")

      val u = createUserStub(friends = List(Friendship(f1.id, FriendshipStatus.Accepted), Friendship(f2.id, FriendshipStatus.Invited)))
      val excludeAuthors = List("aex1", "aex2")
      val levels = Some(1, 2)
      val idsExclude = List("idex1", "idex2")

      db.battle.allWithParams(
        status = any,
        authorIds = any,
        authorIdsExclude = any,
        solutionIds = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        idsExclude = any,
        cultureId = any
      ) returns Iterator.empty

      val result = api.getFriendsBattles(
        GetFriendsBattlesRequest(
          user = u,
          statuses = List(BattleStatus.Fighting),
          idsExclude = idsExclude,
          authorsExclude = excludeAuthors,
          levels = levels))

      there was one(battle).allWithParams(
        status = mEq(List(BattleStatus.Fighting)),
        authorIds = mEq(List(f1.id)),
        authorIdsExclude = mEq(excludeAuthors),
        solutionIds = any,
        levels = mEq(levels),
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = mEq(idsExclude),
        cultureId = mEq(u.demo.cultureId))

      result must beAnInstanceOf[OkApiResult[GetFriendsBattlesResult]]
    }

    "getFollowingBattles return battles for following only" in context {
      val following = List("f1", "f2")
      val u = createUserStub(following = following)
      val excludeAuthors = List("aex1", "aex2")
      val levels = Some(1, 2)
      val idsExclude = List("idex1", "idex2")

      db.battle.allWithParams(
        status = any,
        authorIds = any,
        authorIdsExclude = any,
        solutionIds = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        idsExclude = any,
        cultureId = any
      ) returns Iterator.empty

      val result = api.getFollowingBattles(
        GetFollowingBattlesRequest(
          user = u,
          statuses = List(BattleStatus.Fighting),
          idsExclude = idsExclude,
          authorsExclude = excludeAuthors,
          levels = levels))

      there was one(battle).allWithParams(
        status = mEq(List(BattleStatus.Fighting)),
        authorIds = mEq(following),
        authorIdsExclude = mEq(excludeAuthors),
        solutionIds = any,
        levels = mEq(levels),
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = mEq(idsExclude),
        cultureId = mEq(u.demo.cultureId))

      result must beAnInstanceOf[OkApiResult[GetFollowingBattlesResult]]
    }


    "getVIPBattles return battles for vips only" in context {
      val u = createUserStub()
      val excludeAuthors = List("aex1", "aex2")
      val levels = Some(1, 2)
      val idsExclude = List("idex1", "idex2")

      db.battle.allWithParams(
        status = any,
        authorIds = any,
        authorIdsExclude = any,
        solutionIds = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        idsExclude = any,
        cultureId = any
      ) returns Iterator.empty

      val result = api.getVIPBattles(
        GetVIPBattlesRequest(
          user = u,
          statuses = List(BattleStatus.Fighting),
          idsExclude = idsExclude,
          authorsExclude = excludeAuthors,
          levels = levels))

      there was one(battle).allWithParams(
        status = mEq(List(BattleStatus.Fighting)),
        authorIds = any,
        authorIdsExclude = mEq(excludeAuthors),
        solutionIds = any,
        levels = mEq(levels),
        skip = mEq(0),
        vip = mEq(Some(true)),
        ids = any,
        idsExclude = mEq(idsExclude),
        cultureId = mEq(u.demo.cultureId))

      result must beAnInstanceOf[OkApiResult[GetVIPBattlesResult]]
    }

    "getLikedSolutionBattles calls db correctly" in context {
      val votedSolutions = Map(
        "s1" -> ContentVote.Cheating,
        "s2" -> ContentVote.Cool)
      val u = createUserStub(votedSolutions = votedSolutions)
      val excludeAuthors = List("aex1", "aex2")
      val levels = Some(1, 2)
      val idsExclude = List("idex1", "idex2")

      db.battle.allWithParams(
        status = any,
        authorIds = any,
        authorIdsExclude = any,
        solutionIds = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        idsExclude = any,
        cultureId = any
      ) returns Iterator.empty

      val result = api.getLikedSolutionBattles(
        GetLikedSolutionBattlesRequest(
          user = u,
          statuses = List(BattleStatus.Fighting),
          idsExclude = idsExclude,
          authorsExclude = excludeAuthors,
          levels = levels))

      there was one(battle).allWithParams(
        status = mEq(List(BattleStatus.Fighting)),
        authorIds = any,
        authorIdsExclude = mEq(excludeAuthors),
        solutionIds = mEq(List("s2")),
        levels = mEq(levels),
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = mEq(idsExclude),
        cultureId = mEq(u.demo.cultureId))

      result must beAnInstanceOf[OkApiResult[GetLikedSolutionBattlesResult]]
    }

    "getAllBattles calls db correctly" in context {
      val u = createUserStub()
      val excludeAuthors = List("aex1", "aex2")
      val levels = Some(1, 2)
      val idsExclude = List("idex1", "idex2")

      db.battle.allWithParams(
        status = any,
        authorIds = any,
        authorIdsExclude = any,
        solutionIds = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        idsExclude = any,
        cultureId = any
      ) returns Iterator.empty

      val result = api.getAllBattles(
        GetAllBattlesRequest(
          user = u,
          statuses = List(BattleStatus.Fighting),
          idsExclude = idsExclude,
          authorIdsExclude = excludeAuthors,
          levels = levels))

      there was one(battle).allWithParams(
        status = mEq(List(BattleStatus.Fighting)),
        authorIds = any,
        authorIdsExclude = mEq(excludeAuthors),
        solutionIds = any,
        levels = mEq(levels),
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = mEq(idsExclude),
        cultureId = mEq(u.demo.cultureId))

      result must beAnInstanceOf[OkApiResult[GetAllBattlesResult]]
    }
  }
}

