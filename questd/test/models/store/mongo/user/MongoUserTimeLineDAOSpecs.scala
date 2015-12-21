package models.store.mongo.user

import java.util.Date

import models.domain.user.User
import models.domain.user.timeline.{TimeLineType, TimeLineReason, TimeLineEntry}
import models.store.mongo.BaseDAOSpecs
import play.api.test.WithApplication
import testhelpers.domainstubs._

/**
 * MongoUserTimeLineDAO specs
 */
trait MongoUserTimeLineDAOSpecs { this: BaseDAOSpecs =>
  "Mongo User DAO" should {

    "Add entry to time line" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val u = User(id = "idid_for_time_line")
      val tle1 = TimeLineEntry(
        id = "id",
        reason = TimeLineReason.Created,
        actorId = u.id,
        TimeLineType.Quest,
        objectId = "oid")

      db.user.create(u)
      db.user.addEntryToTimeLine(u.id, tle1)

      val ou1 = db.user.readById(u.id)
      ou1 must beSome[User]
      ou1.get.timeLine must beEqualTo(List(tle1))
    }

    "Add entry to time line multi" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val u = List(User(), User())
      val tle = TimeLineEntry(
        id = "id",
        reason = TimeLineReason.Created,
        actorId = u.head.id,
        TimeLineType.Quest,
        objectId = "oid")

      u.foreach(db.user.create)
      db.user.addEntryToTimeLineMulti(u.map(_.id), tle)

      val ou1 = db.user.readById(u.head.id)
      ou1 must beSome[User]
      ou1.get.timeLine must beEqualTo(List(tle))

      val ou2 = db.user.readById(u(1).id)
      ou2 must beSome[User]
      ou2.get.timeLine must beEqualTo(List(tle))
    }

    "Remove entry from time line" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val userId = "userId"
      val tle1 = TimeLineEntry(
        id = "idqwe",
        reason = TimeLineReason.Created,
        actorId = userId,
        TimeLineType.Quest,
        objectId = "oid")
      val u = User(id = userId, timeLine = List(tle1))

      db.user.create(u)
      db.user.removeEntryFromTimeLineByObjectId(u.id, tle1.objectId)

      val ou1 = db.user.readById(u.id)
      ou1 must beSome[User]
      ou1.get.timeLine must beEqualTo(List.empty)
    }

    "Update entry in time line" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val userId = "userId"
      val tle1 = TimeLineEntry(
        id = "idqwe",
        reason = TimeLineReason.Created,
        actorId = userId,
        TimeLineType.Quest,
        objectId = "oid")
      val u = User(id = userId, timeLine = List(tle1))

      db.user.create(u)
      db.user.updateTimeLineEntry(u.id, tle1.id, TimeLineReason.Hidden)

      val ou1 = db.user.readById(u.id)
      ou1 must beSome[User]
      ou1.get.timeLine must beEqualTo(List(tle1.copy(reason = TimeLineReason.Hidden)))
    }

    "setTimeLinePopulationTime sets it" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val time = new Date(1000)

      val user = createUserStub()

      db.user.create(user)
      db.user.setTimeLinePopulationTime(user.id, time)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User]
      val u = ou1.get
      u.schedules.nextTimeLineAt must beEqualTo(time)
    }
  }
}
