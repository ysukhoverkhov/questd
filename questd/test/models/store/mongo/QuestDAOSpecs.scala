

package models.store.mongo

import org.specs2.mutable._
import play.api.test._
import models.domain._
import testhelpers.domainstubs._

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
          themeId = "t1",
          status = QuestStatus.OnVoting,
          level = 3,
          vip = false,
          cultureId = "c1"),

        createQuestStub(
          id = "q2",
          authorId = "q2_author id",
          themeId = "t2",
          status = QuestStatus.InRotation,
          level = 13,
          vip = true,
          cultureId = "c2"),

        createQuestStub(
          id = "q3",
          authorId = "q3_author id",
          themeId = "t3",
          status = QuestStatus.OnVoting,
          level = 7,
          vip = true,
          cultureId = "c3"))

      qs.foreach(db.quest.create)

      val all = db.quest.allWithParams()
      all.size must beEqualTo(qs.size)

      val status = db.quest.allWithParams(status = List(QuestStatus.OnVoting.toString)).toList
      status.map(_.id).size must beEqualTo(2)
      status.map(_.id) must contain(qs(0).id) and contain(qs(2).id)

      val userids = db.quest.allWithParams(authorIds = List("q2_author id")).toList
      userids.map(_.id) must beEqualTo(List(qs(1).id))

      val levels = db.quest.allWithParams(levels = Some((1, 10))).toList
      levels.map(_.id).size must beEqualTo(2)
      levels.map(_.id) must contain(qs(0).id) and contain(qs(2).id)

      val levelsSkip = db.quest.allWithParams(levels = Some((1, 10)), skip = 1).toList
      levelsSkip.map(_.id).size must beEqualTo(1)
      levelsSkip.map(_.id) must beEqualTo(List(qs(2).id)) or beEqualTo(List(qs(0).id))

      val vip = db.quest.allWithParams(vip = Some(true)).toList
      vip.map(_.id).size must beEqualTo(2)
      vip.map(_.id) must contain(qs(1).id) and contain(qs(2).id)

      val statusVip = db.quest.allWithParams(status = List(QuestStatus.OnVoting.toString), vip = Some(false)).toList
      statusVip.map(_.id).size must beEqualTo(1)
      statusVip.map(_.id) must beEqualTo(List(qs(0).id))

      val ids = db.quest.allWithParams(ids = List("q1", "q2"), vip = Some(true)).toList
      ids.map(_.id).size must beEqualTo(1)
      ids.map(_.id) must beEqualTo(List(qs(1).id))

      val themeIds = db.quest.allWithParams(themeIds = List("t1", "t3")).toList
      themeIds.map(_.id).size must beEqualTo(2)
      themeIds.map(_.id) must contain(qs(0).id) and contain(qs(2).id)

      val themeIdsAndIds = db.quest.allWithParams(ids = List("q1", "q2"), themeIds = List("t1", "t3")).toList
      themeIdsAndIds.map(_.id).size must beEqualTo(1)
      themeIdsAndIds.map(_.id) must beEqualTo(List(qs(0).id))

      val culture = db.quest.allWithParams(cultureId = Some(qs(2).cultureId)).toList
      culture.map(_.id).size must beEqualTo(1)
      culture.map(_.id) must beEqualTo(List(qs(2).id))
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

  }

}

