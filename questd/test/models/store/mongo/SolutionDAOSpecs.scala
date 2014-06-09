

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
import java.util.Date

//@RunWith(classOf[JUnitRunner])
class SolutionDAOSpecs extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  private[this] def clearDB() = {
    db.solution.all.foreach((x) => db.solution.delete(x.id))
  }

  private def createSolution(
    id: String,
    questId: String = "quest id",
    userId: String = "user id",
    themeId: String = "theme id",
    questLevel: Int = 5,
    vip: Boolean = false,
    status: String = QuestSolutionStatus.OnVoting.toString,
    lastModDate: Date = new Date()) = {

    QuestSolution(
      id = id,
      userId = userId,
      questLevel = questLevel,
      info = QuestSolutionInfo(
        content = QuestSolutionInfoContent(media = ContentReference(ContentType.Video, "", "")),
        vip = vip,
        questId = questId,
        themeId = themeId),
      status = status,
      lastModDate = lastModDate)
  }

  private def createSolutionInDB(
    id: String,
    questId: String = "quest id",
    userId: String = "user id",
    themeId: String = "theme id",
    questLevel: Int = 5,
    vip: Boolean = false,
    status: String = QuestSolutionStatus.OnVoting.toString) = {

    db.solution.create(createSolution(id, questId, userId, themeId, questLevel, vip, status))
  }

  "Mongo Quest Solution DAO" should {
    "Create solution and find it by id" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "ididiid"

      createSolutionInDB(id)

      val q = db.solution.readById(id)

      q must beSome[QuestSolution].which(_.id == id)
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

      q2 must beSome[QuestSolution].which(_.id == id) and beSome[QuestSolution].which(_.info.content.media.reference == "3")
    }

    "Delete quest solution" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "ididiid"

      createSolutionInDB(id)

      val q = db.solution.readById(id)

      q must beSome[QuestSolution].which(_.id == id)

      db.solution.delete(id)
      db.solution.readById(id) must beNone
    }

    "Get all solutions with params" in new WithApplication(appWithTestDatabase) {
      clearDB()

      // Preparing quests to store in db.
      val qs = List(
        createSolution("q1", "t1", "q1_author id", "q1_theme_id", 3, false, QuestStatus.OnVoting.toString, new Date(5)),
        createSolution("q2", "t2", "q2_author id", "q2_theme_id", 13, true, QuestStatus.InRotation.toString, new Date(3)),
        createSolution("q3", "t3", "q3_author id", "q3_theme_id", 7, true, QuestStatus.OnVoting.toString, new Date(4)))

      qs.foreach(db.solution.create)

      val all = db.solution.allWithParams().toList
      all.size must beEqualTo(qs.size)
      // Checking order with lastModDate
      all must beEqualTo(List(qs(1), qs(2), qs(0)))

      val status = db.solution.allWithParams(status = Some(QuestStatus.OnVoting.toString)).toList
      status.map(_.id).size must beEqualTo(2)
      status.map(_.id) must contain(qs(0).id) and contain(qs(2).id)

      val userids = db.solution.allWithParams(userIds = List("q2_author id")).toList
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

      val statusVip = db.solution.allWithParams(status = Some(QuestStatus.OnVoting.toString), vip = Some(false)).toList
      statusVip.map(_.id).size must beEqualTo(1)
      statusVip.map(_.id) must beEqualTo(List(qs(0).id))

      val ids = db.solution.allWithParams(ids = List("q1", "q2"), vip = Some(true)).toList
      ids.map(_.id).size must beEqualTo(1)
      ids.map(_.id) must beEqualTo(List(qs(1).id))

      val questIds = db.solution.allWithParams(questIds = List("t1", "t3")).toList
      questIds.map(_.id).size must beEqualTo(2)
      questIds.map(_.id) must contain(qs(0).id) and contain(qs(2).id)

      val themeIds = db.solution.allWithParams(themeIds = List("q1_theme_id", "q2_theme_id")).toList
      themeIds.map(_.id).size must beEqualTo(2)
      themeIds.map(_.id) must contain(qs(0).id) and contain(qs(1).id)
      
      val themeIdsAndIds = db.solution.allWithParams(ids = List("q1", "q2"), questIds = List("t1", "t3")).toList
      themeIdsAndIds.map(_.id).size must beEqualTo(1)
      themeIdsAndIds.map(_.id) must beEqualTo(List(qs(0).id))
    }

  }

}

