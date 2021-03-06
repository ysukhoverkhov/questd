

package models.store.mongo

import models.domain.common.{ContentReference, ContentType}
import models.domain.quest.{Quest, QuestStatus}
import org.specs2.mutable._
import play.api.test._
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
//@RunWith(classOf[JUnitRunner])
class QuestDAOSpecs extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  private[this] def clearDB() = {
    db.quest.clear()
  }

  "Mongo Quest DAO" should {
    "Create quest and find it by id" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "ididiid"

      db.quest.create(createQuestStub(id))

      val q = db.quest.readById(id)

      q must beSome[Quest]
      q.get.id must beEqualTo(id)
    }

    "Update quest" in new WithApplication(appWithTestDatabase) {
      clearDB()
      val id = "ididiid"

      db.quest.create(createQuestStub(id))

      val q = db.quest.readById(id)

      db.quest.update(q.get.copy(info = q.get.info.copy(
        content = q.get.info.content.copy(
          media = ContentReference(ContentType.Video, "2", "3")))))

      val q2 = db.quest.readById(id)

      q2 must beSome[Quest]
      q2.get.id must beEqualTo(id)
      q2.get.info.content.media.reference must beEqualTo("3")
    }

    "Delete quest" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "ididiid"

      db.quest.create(createQuestStub(id))

      val q = db.quest.readById(id)

      q must beSome[Quest]
      q.get.id must beEqualTo(id)

      db.quest.delete(id)
      db.quest.readById(id) must beNone
    }

    "Get all quests" in new WithApplication(appWithTestDatabase) {

      clearDB()

      // Preparing quests to store in db.

      val qs = List(
        createQuestStub(
          id = "q1",
          authorId = "q1_author id",
          status = QuestStatus.CheatingBanned,
          level = 3,
          vip = false,
          cultureId = "c1",
          timelinePoints = 321),

        createQuestStub(
          id = "q2",
          authorId = "q2_author id",
          status = QuestStatus.InRotation,
          level = 13,
          vip = true,
          cultureId = "c2",
          timelinePoints = 21,
          solutionsCount = 1),

        createQuestStub(
          id = "q3",
          authorId = "q3_author id",
          status = QuestStatus.AdminBanned,
          level = 7,
          vip = true,
          cultureId = "c3",
          timelinePoints = 60))

      qs.foreach(db.quest.create)

      // Sorted by points.
      val all = db.quest.allWithParams().toList
      all.size must beEqualTo(qs.size)
      all.map(_.id) must beEqualTo(qs.sortBy(_.rating.timelinePoints)(Ordering[Int].reverse).map(_.id))

      val status = db.quest.allWithParams(status = List(QuestStatus.CheatingBanned, QuestStatus.AdminBanned)).toList
      status.map(_.id).size must beEqualTo(2)
      status.map(_.id) must contain(qs(0).id) and contain(qs(2).id)

      val userIds = db.quest.allWithParams(authorIds = List("q2_author id")).toList
      userIds.map(_.id) must beEqualTo(List(qs(1).id))

      val levels = db.quest.allWithParams(levels = Some((1, 10))).toList
      levels.map(_.id).size must beEqualTo(2)
      levels.map(_.id) must contain(qs(0).id) and contain(qs(2).id)

      val levelsSkip = db.quest.allWithParams(levels = Some((1, 10)), skip = 1).toList
      levelsSkip.map(_.id).size must beEqualTo(1)
      levelsSkip.map(_.id) must beEqualTo(List(qs(2).id)) or beEqualTo(List(qs(0).id))

      val vip = db.quest.allWithParams(vip = Some(true)).toList
      vip.map(_.id).size must beEqualTo(2)
      vip.map(_.id) must contain(qs(1).id) and contain(qs(2).id)

      val statusVip = db.quest.allWithParams(status = List(QuestStatus.CheatingBanned), vip = Some(false)).toList
      statusVip.map(_.id).size must beEqualTo(1)
      statusVip.map(_.id) must beEqualTo(List(qs(0).id))

      val ids = db.quest.allWithParams(ids = List("q1", "q2"), vip = Some(true)).toList
      ids.map(_.id).size must beEqualTo(1)
      ids.map(_.id) must beEqualTo(List(qs(1).id))

      val culture = db.quest.allWithParams(cultureId = Some(qs(2).cultureId)).toList
      culture.map(_.id).size must beEqualTo(1)
      culture.map(_.id) must beEqualTo(List(qs(2).id))

      val excludingIds = db.quest.allWithParams(idsExclude = List("q1", "q2")).toList
      excludingIds.map(_.id).size must beEqualTo(1)
      excludingIds.map(_.id) must beEqualTo(List(qs(2).id))

      val excludingAuthorIds = db.quest.allWithParams(authorIdsExclude = List("q2_author id")).toList
      excludingAuthorIds.map(_.id).size must beEqualTo(2)
      excludingAuthorIds.map(_.id).sorted must beEqualTo(List(qs(0).id, qs(2).id).sorted)

      val withSolutions = db.quest.allWithParams(withSolutions = Some(true)).toList
      withSolutions.map(_.id).size must beEqualTo(1)
      withSolutions.map(_.id).sorted must beEqualTo(List(qs(1).id).sorted)
    }

    "Replace cultures" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val t1 = createQuestStub(id = "id1", cultureId = "rus")
      val t2 = createQuestStub(id = "id2", cultureId = "eng")
      val t3 = createQuestStub(id = "id3", cultureId = "rus")

      db.quest.create(t1)
      db.quest.create(t2)
      db.quest.create(t3)

      db.quest.replaceCultureIds("rus", "eng")

      val ou1 = db.quest.readById(t1.id)
      ou1 must beSome.which((u: Quest) => u.id == t1.id)
      ou1 must beSome.which((u: Quest) => u.cultureId == "eng")

      val ou2 = db.quest.readById(t2.id)
      ou2 must beSome.which((u: Quest) => u.id == t2.id)
      ou2 must beSome.which((u: Quest) => u.cultureId == "eng")

      val ou3 = db.quest.readById(t3.id)
      ou3 must beSome.which((u: Quest) => u.id == t3.id)
      ou3 must beSome.which((u: Quest) => u.cultureId == "eng")
    }

    "Update quest points"  in new WithApplication(appWithTestDatabase) {
      clearDB()

      val quest = createQuestStub()

      db.quest.create(quest)

      db.quest.updatePoints(
        id = quest.id,
        timelinePointsChange = 1,
        likesChange = 2,
        votersCountChange = 3,
        cheatingChange = 4,
        spamChange = 5,
        pornChange = 6)

      val ou1 = db.quest.readById(quest.id)
      ou1 must beSome.which((q: Quest) => q.id == quest.id)
      ou1 must beSome.which((u: Quest) => u.rating.timelinePoints == 1)
      ou1 must beSome.which((u: Quest) => u.rating.likesCount == 2)
      ou1 must beSome.which((u: Quest) => u.rating.votersCount == 3)
      ou1 must beSome.which((u: Quest) => u.rating.cheating == 4)
      ou1 must beSome.which((u: Quest) => u.rating.iacpoints.spam == 5)
      ou1 must beSome.which((u: Quest) => u.rating.iacpoints.porn == 6)
    }

    "Adds solution id"  in new WithApplication(appWithTestDatabase) {
      clearDB()

      val quest = createQuestStub()
      val solutionId = "solutionId"

      db.quest.create(quest)

      db.quest.addSolution(quest.id)

      val ou1 = db.quest.readById(quest.id)
      ou1 must beSome.which((q: Quest) => q.solutionsCount == 1)
    }

  }

}

