

package models.store.mongo

import org.specs2.mutable._
import play.api.test._
import models.domain._
import java.util.Date
import testhelpers.domainstubs._

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
    status: QuestSolutionStatus.Value = QuestSolutionStatus.OnVoting) = {

    db.solution.create(createSolutionStub(
      id = id,
      questId = questId,
      userId = userId,
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
        createSolutionStub(
          id = "q1",
          questId = "t1",
          userId = "q1_author id",
          themeId = "q1_theme_id",
          level = 3,
          vip = false,
          status = QuestSolutionStatus.OnVoting,
          voteEndDate = new Date(5000),
          lastModDate = new Date(5000)),
        createSolutionStub(
          id = "q2",
          questId = "t2",
          userId = "q2_author id",
          themeId = "q2_theme_id",
          level = 13,
          vip = true,
          status = QuestSolutionStatus.Won,
          voteEndDate = new Date(3000),
          lastModDate = new Date(3000)),
        createSolutionStub(
          id = "q3",
          questId = "t3",
          userId  = "q3_author id",
          themeId = "q3_theme_id",
          level = 7,
          vip = true,
          status = QuestSolutionStatus.OnVoting,
          voteEndDate = new Date(4000),
          lastModDate = new Date(4000)))

      qs.foreach(db.solution.create)

      val all = db.solution.allWithParams().toList
      all.size must beEqualTo(qs.size)
      // Checking order with lastModDate
      all must beEqualTo(List(qs(1), qs(2), qs(0)))

      val status = db.solution.allWithParams(status = List(QuestStatus.OnVoting.toString)).toList
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

      val statusVip = db.solution.allWithParams(status = List(QuestStatus.OnVoting.toString), vip = Some(false)).toList
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

      val statusWithQuestIds = db.solution.allWithParams(ids = List("q1", "q2"), status = List(QuestStatus.OnVoting.toString)).toList
      ids.map(_.id).size must beEqualTo(1)
      ids.map(_.id) must beEqualTo(List(qs(1).id))
    }

    "updateStatus for solution updates rival correctly" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id1 = "id1"
      val id2 = "id2"
      createSolutionInDB(id1)
      createSolutionInDB(id2)

      val su1 = db.solution.updateStatus(id1, QuestSolutionStatus.Lost.toString)
      val su2 = db.solution.updateStatus(id2, QuestSolutionStatus.Won.toString, Some(id1))

      su1 must beSome[QuestSolution].which(s => s.status == QuestSolutionStatus.Lost && s.rivalSolutionId == None)
      su2 must beSome[QuestSolution].which(s => s.status == QuestSolutionStatus.Won && s.rivalSolutionId == Some(id1))
    }

  }

}

