

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
    for (i <- db.quest.allQuests)
      db.quest.delete(i.id)
  }

  "Mongo Quest DAO" should {
    "Create quest and find it by id" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "ididiid"

      db.quest.createQuest(Quest(id))
      val q = db.quest.readQuestByID(id)

      q must beSome[Quest]
      q.get.id must beEqualTo(QuestID(id))
    }

    "Update quest" in new WithApplication(appWithTestDatabase) {
      clearDB()
      val id = "ididiid"

      db.quest.createQuest(Quest(id))
      val q = db.quest.readQuestByID(id)
      q.get.info.content.reference must beEqualTo("")

      db.quest.updateQuest(q.get.copy(info = QuestInfo(ContentReference(1, "2", "3"))))
      
      val q2 = db.quest.readQuestByID(id)
      
      q2 must beSome[Quest]
      q2.get.id must beEqualTo(QuestID(id))
      q2.get.info.content.reference must beEqualTo("3")
    }

    "Delete quest" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "ididiid"

      db.quest.createQuest(Quest(id))
      val q = db.quest.readQuestByID(id)

      q must beSome[Quest]
      q.get.id must beEqualTo(QuestID(id))
      
      db.quest.deleteQuest(id)
      db.quest.readQuestByID(id) must beNone
    }

  }

}

