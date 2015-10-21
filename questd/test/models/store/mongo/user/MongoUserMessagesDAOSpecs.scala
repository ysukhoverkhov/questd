package models.store.mongo.user

import java.util.Date

import models.domain.common.ClientPlatform
import models.domain.user.devices.Device
import models.domain.user.message.MessageInformation
import models.domain.user.{User, message}
import models.store.mongo.BaseDAOSpecs
import play.api.test.WithApplication
import testhelpers.domainstubs._

/**
 * MongoUserMessagesDAO specs
 */
trait MongoUserMessagesDAOSpecs { this: BaseDAOSpecs =>
  "Mongo User DAO" should {

    "addMessage adds a message" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val m = message.MessageDailyResultsReady().toMessage

      db.user.create(user)
      db.user.addMessage(user.id, m)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User].which(_.profile.messages == List(m))
    }

    "addMessageToEveryone does its work" in new WithApplication(appWithTestDatabase) {
      val users = (1 to 5).map(i => createUserStub())

      users.foreach(db.user.create)
      db.user.addMessageToEveryone(MessageInformation("", Some("")))

      val u: Iterator[User] = db.user.all
      val u2 = u.toList
      //noinspection ZeroIndexToHead
      u2(0).profile.messages.length must beEqualTo(1)
      u2(1).profile.messages.length must beEqualTo(1)
      u2(2).profile.messages.length must beEqualTo(1)
      u2(3).profile.messages.length must beEqualTo(1)
      u2(4).profile.messages.length must beEqualTo(1)
    }

    "removeMessage removes a message" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val m = message.MessageDailyResultsReady().toMessage

      db.user.create(user)
      db.user.addMessage(user.id, m)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User].which(_.profile.messages == List(m))

      db.user.removeMessage(user.id, m.id)

      val ou2 = db.user.readById(user.id)
      ou2 must beSome[User].which(_.profile.messages == List.empty)
    }

    "removeOldestMessage removes a really oldest message" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val ms = (1 to 5).map(n => message.MessageDailyResultsReady().toMessage)

      db.user.create(user)
      ms.foreach(db.user.addMessage(user.id, _))

      db.user.removeOldestMessage(user.id)

      val ou2 = db.user.readById(user.id)
      ou2 must beSome[User].which(_.profile.messages == ms.tail)
    }

    "addDevice and remove device works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val device = Device(ClientPlatform.iPhone, "token")

      db.user.create(user)
      db.user.addDevice(user.id, device)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User].which(u => u.devices.size == 1 && u.devices.head == device)

      db.user.removeDevice(user.id, device.token)

      val ou2 = db.user.readById(user.id)
      ou2 must beSome[User].which(u => u.devices.isEmpty)
    }

    "removeDevice works" in new WithApplication(appWithTestDatabase) {
      success
    }

    "setNotificationSentTime works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val date = new Date()

      db.user.create(user)
      db.user.setNotificationSentTime(user.id, date)

      val ou = db.user.readById(user.id)

      ou must beSome
      ou.get.schedules.lastNotificationSentAt must beEqualTo(date)
    }

    "Devices are added and removed" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val d1 = Device(ClientPlatform.iPhone, "d1")
      val d2 = Device(ClientPlatform.iPhone, "d2")

      val user = createUserStub()
      user.devices must beEqualTo(List.empty)

      db.user.create(user)

      val ou1 = db.user.addDevice(user.id, d1)
      ou1.get.devices must beEqualTo(List(d1))

      val ou2 = db.user.addDevice(user.id, d2)
      ou2.get.devices must beEqualTo(List(d1, d2))

      val ou3 = db.user.removeDevice(user.id, d2.token)
      ou3.get.devices must beEqualTo(List(d1))
    }
  }
}
