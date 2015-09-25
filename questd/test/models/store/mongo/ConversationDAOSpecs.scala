

package models.store.mongo

import models.domain.chat.{Participant, Conversation}
import org.specs2.mutable._
import play.api.test._
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class ConversationDAOSpecs extends BaseDAOSpecs {

  "Mongo Conversation DAO" should {
    "Search conversation by participant id" in new WithApplication(appWithTestDatabase) {
      db.conversation.clear()

      val parts: List[Participant] = List("1", "2", "3").map(Participant(_))

      val convers: List[Conversation] = List(
        Conversation(participants = List(parts(0), parts(1))),
        Conversation(participants = List(parts(0), parts(2))),
        Conversation(participants = List(parts(1), parts(2)))
      )

      convers.foreach(db.conversation.create)

      val convs = db.conversation.findByParticipant(parts(1).userId).toList
      convs must beEqualTo(List(convers(0), convers(2)))

      val allConvs = db.conversation.findByAllParticipants(List(parts(1).userId, parts(0).userId)).toList
      allConvs must beEqualTo(List(convers(0)))
    }

    "Sets unread message flag" in new WithApplication(appWithTestDatabase) {
      db.conversation.clear()

      val ps = List("1", "2")
      val conv = createConversationStub(pIds = ps)
      db.conversation.create(conv)

      db.conversation.setUnreadMessagesFlag(conv.id, ps(0), flag = true)
      db.conversation.setUnreadMessagesFlag(conv.id, ps(1), flag = false)

      val c = db.conversation.readById(conv.id).get

      c.participants(0).hasUnreadMessages must beEqualTo(true)
      c.participants(1).hasUnreadMessages must beEqualTo(false)
    }
  }
}

