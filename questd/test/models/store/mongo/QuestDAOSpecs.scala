

package models.store.mongo

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.Logger
import com.mongodb.casbah.commons.MongoDBObject
import models.store._
import models.domain._

//@RunWith(classOf[JUnitRunner])
class QuestDAOSpecs extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  private[this] def clearDB() = {
    for (i <- db.quest.all)
      db.quest.delete(i.id)
  }

  "Mongo Quest DAO" should {
    "Create quest and find it by id" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "ididiid"

      db.quest.create(Quest(
        id = id,

        authorUserId = "user id",
        approveReward = Assets(),
        info = QuestInfo(
          themeId = "theme_id",
          content = QuestInfoContent(
            media = ContentReference(ContentType.Video.toString, "", ""),
            icon = None,
            description = "The description"),
          vip = true)))

      val q = db.quest.readById(id)

      q must beSome[Quest]
      q.get.id must beEqualTo(id)
    }

    "Update quest" in new WithApplication(appWithTestDatabase) {
      clearDB()
      val id = "ididiid"

      db.quest.create(Quest(
        id = id,
        authorUserId = "user id",
        approveReward = Assets(),
        info = QuestInfo(
          themeId = "theme_id",
          content = QuestInfoContent(
            media = ContentReference(ContentType.Video.toString, "", ""),
            icon = None,
            description = "The description"),
          vip = true)))

      val q = db.quest.readById(id)
      q.get.info.content.media.reference must beEqualTo("")

      db.quest.update(q.get.copy(info = q.get.info.copy(
        content = q.get.info.content.copy(
          media = ContentReference(ContentType.Video.toString, "2", "3")))))

      val q2 = db.quest.readById(id)

      q2 must beSome[Quest]
      q2.get.id must beEqualTo(id)
      q2.get.info.content.media.reference must beEqualTo("3")
    }

    "Delete quest" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "ididiid"

      db.quest.create(Quest(
        id = id,
        authorUserId = "user id",
        approveReward = Assets(),
        info = QuestInfo(
          themeId = "theme_id",
          content = QuestInfoContent(
            media = ContentReference(ContentType.Video.toString, "", ""),
            icon = None,
            description = "The description"),
          vip = true)))

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
        Quest(
          id = "q1",
          authorUserId = "q1_author id",
          approveReward = Assets(),
          info = QuestInfo(
            themeId = "t1",
            content = QuestInfoContent(
              media = ContentReference(ContentType.Video.toString, "", ""),
              icon = None,
              description = "The description"),
            level = 3,
            vip = false),
          status = QuestStatus.OnVoting.toString),

        Quest(
          id = "q2",
          authorUserId = "q2_author id",
          approveReward = Assets(),
          info = QuestInfo(
            themeId = "t2",
            content = QuestInfoContent(
              media = ContentReference(ContentType.Video.toString, "", ""),
              icon = None,
              description = "The description"),
            level = 13,
            vip = true),
          status = QuestStatus.InRotation.toString),

        Quest(
          id = "q3",
          authorUserId = "q3_author id",
          approveReward = Assets(),
          info = QuestInfo(
            themeId = "t3",
            content = QuestInfoContent(
              media = ContentReference(ContentType.Video.toString, "", ""),
              icon = None,
              description = "The description"),
            level = 7,
            vip = true),
          status = QuestStatus.OnVoting.toString))

      qs.foreach(db.quest.create)

      val all = db.quest.allWithParams()
      all.size must beEqualTo(qs.size)

      val status = db.quest.allWithParams(status = Some(QuestStatus.OnVoting.toString)).toList
      status.map(_.id).size must beEqualTo(2)
      status.map(_.id) must contain(qs(0).id) and contain(qs(2).id)

      val userids = db.quest.allWithParams(userIds = List("q2_author id")).toList
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

      val statusVip = db.quest.allWithParams(status = Some(QuestStatus.OnVoting.toString), vip = Some(false)).toList
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
    }

  }

}

