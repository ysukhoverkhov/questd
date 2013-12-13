

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

      db.quest.create(Quest(id, QuestInfo(
        themeID = "theme_id",
        authorUserID = "user id",
        content = QuestInfoContent(

          media = ContentReference(ContentType.Video.toString, "", ""),
          icon = ContentReference(ContentType.Video.toString, "", ""),
          description = "The description"))))

      val q = db.quest.readByID(id)

      q must beSome[Quest]
      q.get.id must beEqualTo(id)
    }

    "Update quest" in new WithApplication(appWithTestDatabase) {
      clearDB()
      val id = "ididiid"

      db.quest.create(Quest(id, QuestInfo(
        themeID = "theme_id",
        authorUserID = "user id",
        content = QuestInfoContent(

          media = ContentReference(ContentType.Video.toString, "", ""),
          icon = ContentReference(ContentType.Video.toString, "", ""),
          description = "The description"))))

      val q = db.quest.readByID(id)
      q.get.info.content.media.reference must beEqualTo("")

      db.quest.update(q.get.copy(info = q.get.info.copy(
        content = q.get.info.content.copy(
          media = ContentReference(ContentType.Video.toString, "2", "3")))))

      val q2 = db.quest.readByID(id)

      q2 must beSome[Quest]
      q2.get.id must beEqualTo(id)
      q2.get.info.content.media.reference must beEqualTo("3")
    }

    "Delete quest" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "ididiid"

      db.quest.create(Quest(id, QuestInfo(
        themeID = "theme_id",
        authorUserID = "user id",
        content = QuestInfoContent(

          media = ContentReference(ContentType.Video.toString, "", ""),
          icon = ContentReference(ContentType.Video.toString, "", ""),
          description = "The description"))))

      val q = db.quest.readByID(id)

      q must beSome[Quest]
      q.get.id must beEqualTo(id)

      db.quest.delete(id)
      db.quest.readByID(id) must beNone
    }

  }

}

