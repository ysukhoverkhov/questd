

package models.store.mongo

import models.domain._
import org.specs2.mutable._
import play.api.test.WithApplication
import testhelpers.domainstubs._

//@RunWith(classOf[JUnitRunner])
class BattleDAOSpecs extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  private[this] def clearDB() = {
    db.battle.clear()
  }

  "Mongo Battle DAO" should {

    "Get all battles" in new WithApplication(appWithTestDatabase) {

      clearDB()

      // Preparing quests to store in db.

      val bs = List(
        createBattleStub(
          id = "b1",
          solutionIds = List("s1_id", "s2_id"),
          status = BattleStatus.Fighting,
          level = 3,
          vip = false,
          cultureId = "c1"),

        createBattleStub(
          id = "b2",
          solutionIds = List("s1_id", "s3_id"),
          status = BattleStatus.Resolved,
          level = 7,
          vip = false,
          cultureId = "c1"),

        createBattleStub(
          id = "b3",
          solutionIds = List("s1_id", "s2_id"),
          status = BattleStatus.Fighting,
          level = 3,
          vip = true,
          cultureId = "c2"))

      bs.foreach(db.battle.create)

      val all = db.battle.allWithParams().toList
      all.size must beEqualTo(bs.size)
      all.map(_.id).sorted must beEqualTo(List(bs(0).id, bs(2).id, bs(1).id).sorted)

      val status = db.battle.allWithParams(status = List(BattleStatus.Fighting)).toList
      status.map(_.id).size must beEqualTo(2)
      status.map(_.id) must contain(bs(0).id) and contain(bs(2).id)

//      val userids = db.quest.allWithParams(authorIds = List("q2_author id")).toList
//      userids.map(_.id) must beEqualTo(List(bs(1).id))
//
      val levels = db.battle.allWithParams(levels = Some((5, 10))).toList
      levels.map(_.id).size must beEqualTo(1)
      levels.map(_.id) must beEqualTo(List(bs(1).id))

      val levelsSkip = db.battle.allWithParams(levels = Some((1, 4)), skip = 1).toList
      levelsSkip.map(_.id).size must beEqualTo(1)
      levelsSkip.map(_.id) must beEqualTo(List(bs(2).id)) or beEqualTo(List(bs(0).id))

      val vip = db.battle.allWithParams(vip = Some(true)).toList
      vip.map(_.id).size must beEqualTo(1)
      vip.map(_.id) must beEqualTo(List(bs(2).id))

      val statusVip = db.battle.allWithParams(status = List(BattleStatus.Fighting), vip = Some(false)).toList
      statusVip.map(_.id).size must beEqualTo(1)
      statusVip.map(_.id) must beEqualTo(List(bs(0).id))

      val ids = db.battle.allWithParams(ids = List("b1", "b2"), vip = Some(false)).toList
      ids.map(_.id).size must beEqualTo(2)
      ids.map(_.id).sorted must beEqualTo(List(bs(0).id, bs(1).id).sorted)

      val culture = db.battle.allWithParams(cultureId = Some(bs(2).cultureId)).toList
      culture.map(_.id).size must beEqualTo(1)
      culture.map(_.id) must beEqualTo(List(bs(2).id))

      val excludingIds = db.battle.allWithParams(idsExclude = List("b1", "b2")).toList
      excludingIds.map(_.id).size must beEqualTo(1)
      excludingIds.map(_.id) must beEqualTo(List(bs(2).id))

//      val excludingAuthorIds = db.quest.allWithParams(authorIdsExclude = List("q2_author id")).toList
//      excludingAuthorIds.map(_.id).size must beEqualTo(2)
//      excludingAuthorIds.map(_.id).sorted must beEqualTo(List(bs(0).id, bs(2).id).sorted)
    }


    "Update battle" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val battle = createBattleStub(status = BattleStatus.Fighting)

      db.battle.create(battle)
      db.battle.updateStatus(battle.id, BattleStatus.Resolved)

      var r = db.battle.readById(battle.id)

      r must beSome.which(_.info.status == BattleStatus.Resolved)
    }
  }
}

