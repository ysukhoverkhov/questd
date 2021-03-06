

package models.store.mongo

import java.util.Date

import models.domain.common.{ContentReference, ContentType}
import models.domain.solution.{Solution, SolutionStatus}
import org.specs2.mutable._
import play.api.test._
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
//@RunWith(classOf[JUnitRunner])
class SolutionDAOSpecs extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  private[this] def clearDB() = {
    db.solution.clear()
  }

  private def createSolutionInDB(
    id: String,
    questId: String = "quest id",
    userId: String = "user id",
    themeId: String = "theme id",
    questLevel: Int = 5,
    vip: Boolean = false,
    status: SolutionStatus.Value = SolutionStatus.InRotation) = {

    db.solution.create(createSolutionStub(
      id = id,
      questId = questId,
      authorId = userId,
      themeId = themeId,
      level = questLevel,
      vip = vip,
      status = status))
  }

  "Mongo Quest Solution DAO" should {
    "Create solution and find it by id" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "ididiid"

      createSolutionInDB(id)

      val q = db.solution.readById(id)

      q must beSome[Solution].which(_.id == id)
    }

    "Update quest solution" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "ididiid"

      createSolutionInDB(id)

      val q = db.solution.readById(id)
      q.get.info.content.media.reference must beEqualTo("")

      db.solution.update(q.get.copy(info = q.get.info.copy(
        content = q.get.info.content.copy(
          media = ContentReference(ContentType.Video, "2", "3")))))

      val q2 = db.solution.readById(id)

      q2 must beSome[Solution].which(_.id == id) and beSome[Solution].which(_.info.content.media.reference == "3")
    }

    "Delete quest solution" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "ididiid"

      createSolutionInDB(id)

      val q = db.solution.readById(id)

      q must beSome[Solution].which(_.id == id)

      db.solution.delete(id)
      db.solution.readById(id) must beNone
    }

    "Get all solutions with params" in new WithApplication(appWithTestDatabase) {
      clearDB()

      // Preparing quests to store in db.
      val qs = List(
        createSolutionStub(
          id = "q1",
          questId = "t1",
          authorId = "q1_author id",
          themeId = "q1_theme_id",
          level = 3,
          vip = false,
          status = SolutionStatus.InRotation,
          lastModDate = new Date(5000),
          timelinePoints = 321,
          battleIds = List("b1")),
        createSolutionStub(
          id = "q2",
          questId = "t2",
          authorId = "q2_author id",
          themeId = "q2_theme_id",
          level = 13,
          vip = true,
          status = SolutionStatus.OldBanned,
          lastModDate = new Date(3000),
          timelinePoints = 21),
        createSolutionStub(
          id = "q3",
          questId = "t3",
          authorId  = "q3_author id",
          themeId = "q3_theme_id",
          level = 7,
          vip = true,
          status = SolutionStatus.InRotation,
          lastModDate = new Date(4000),
          timelinePoints = 70,
          battleIds = List("b2")))

      qs.foreach(db.solution.create)

      val all = db.solution.allWithParams().toList
      all.size must beEqualTo(qs.size)
      all.map(_.id) must beEqualTo(qs.sortBy(_.rating.timelinePoints)(Ordering[Int].reverse).map(_.id))

      val status = db.solution.allWithParams(status = List(SolutionStatus.InRotation)).toList
      status.map(_.id).size must beEqualTo(2)
      status.map(_.id) must contain(qs(0).id) and contain(qs(2).id)

      val userids = db.solution.allWithParams(authorIds = List("q2_author id")).toList
      userids.map(_.id) must beEqualTo(List(qs(1).id))

      val levels = db.solution.allWithParams(levels = Some((1, 10))).toList
      levels.map(_.id).size must beEqualTo(2)
      levels.map(_.id) must contain(qs(0).id) and contain(qs(2).id)

      val levelsSkip = db.solution.allWithParams(levels = Some((1, 10)), skip = 1).toList
      levelsSkip.map(_.id).size must beEqualTo(1)
      levelsSkip.map(_.id) must beEqualTo(List(qs(2).id)) or beEqualTo(List(qs(0).id))

      val vip = db.solution.allWithParams(vip = Some(true)).toList
      vip.map(_.id).size must beEqualTo(2)
      vip.map(_.id) must contain(qs(1).id) and contain(qs(2).id)

      val statusVip = db.solution.allWithParams(status = List(SolutionStatus.InRotation), vip = Some(false)).toList
      statusVip.map(_.id).size must beEqualTo(1)
      statusVip.map(_.id) must beEqualTo(List(qs(0).id))

      val ids = db.solution.allWithParams(ids = List("q1", "q2"), vip = Some(true)).toList
      ids.map(_.id).size must beEqualTo(1)
      ids.map(_.id) must beEqualTo(List(qs(1).id))

      val questIds = db.solution.allWithParams(questIds = List("t1", "t3")).toList
      questIds.map(_.id).size must beEqualTo(2)
      questIds.map(_.id) must contain(qs(0).id) and contain(qs(2).id)

//      val themeIds = db.solution.allWithParams(themeIds = List("q1_theme_id", "q2_theme_id")).toList
//      themeIds.map(_.id).size must beEqualTo(2)
//      themeIds.map(_.id) must contain(qs(0).id) and contain(qs(1).id)

      val themeIdsAndIds = db.solution.allWithParams(ids = List("q1", "q2"), questIds = List("t1", "t3")).toList
      themeIdsAndIds.map(_.id).size must beEqualTo(1)
      themeIdsAndIds.map(_.id) must beEqualTo(List(qs(0).id))

      val statusWithQuestIds = db.solution.allWithParams(ids = List("q1", "q2"), status = List(SolutionStatus.InRotation)).toList
      statusWithQuestIds.map(_.id).size must beEqualTo(1)
      statusWithQuestIds.map(_.id) must beEqualTo(List(qs(0).id))

      val excludingIds = db.solution.allWithParams(idsExclude = List("q1", "q2")).toList
      excludingIds.map(_.id).size must beEqualTo(1)
      excludingIds.map(_.id) must beEqualTo(List(qs(2).id))

      val excludingAuthorIds = db.solution.allWithParams(authorIdsExclude = List("q2_author id")).toList
      excludingAuthorIds.map(_.id).size must beEqualTo(2)
      excludingAuthorIds.map(_.id).sorted must beEqualTo(List(qs(0).id, qs(2).id).sorted)

      val withBattles = db.solution.allWithParams(withBattles = Some(true)).toList
      withBattles.map(_.id).size must beEqualTo(2)
      withBattles.map(_.id).sorted must beEqualTo(List(qs(0).id, qs(2).id).sorted)

      val withoutBattles = db.solution.allWithParams(withBattles = Some(false)).toList
      withoutBattles.map(_.id).size must beEqualTo(1)
      withoutBattles.map(_.id).sorted must beEqualTo(List(qs(1).id).sorted)
    }

    "updateStatus for solution works" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id1 = "id1"
      val id2 = "id2"
      createSolutionInDB(id1)
      createSolutionInDB(id2)

      val su1 = db.solution.updateStatus(id1, SolutionStatus.CheatingBanned)
      val su2 = db.solution.updateStatus(id2, SolutionStatus.OldBanned)

      su1 must beSome[Solution].which(s => s.status == SolutionStatus.CheatingBanned && s.battleIds == List.empty)
      su2 must beSome[Solution].which(s => s.status == SolutionStatus.OldBanned && s.battleIds == List.empty)
    }

    "participateInBattle for solution updates rival correctly" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id1 = "id1"
      createSolutionInDB(id1)

      val su1 = db.solution.addParticipatedBattle(id1, "bid")

      su1 must beSome[Solution].which(s =>  s.battleIds == List("bid"))
    }

    "Replace cultures" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val t1 = createSolutionStub(id = "id1", cultureId = "rus")
      val t2 = createSolutionStub(id = "id2", cultureId = "eng")
      val t3 = createSolutionStub(id = "id3", cultureId = "rus")

      db.solution.create(t1)
      db.solution.create(t2)
      db.solution.create(t3)

      db.solution.replaceCultureIds("rus", "eng")

      val ou1 = db.solution.readById(t1.id)
      ou1 must beSome.which((u: Solution) => u.id == t1.id)
      ou1 must beSome.which((u: Solution) => u.cultureId == "eng")

      val ou2 = db.solution.readById(t2.id)
      ou2 must beSome.which((u: Solution) => u.id == t2.id)
      ou2 must beSome.which((u: Solution) => u.cultureId == "eng")

      val ou3 = db.solution.readById(t3.id)
      ou3 must beSome.which((u: Solution) => u.id == t3.id)
      ou3 must beSome.which((u: Solution) => u.cultureId == "eng")
    }
  }
}

