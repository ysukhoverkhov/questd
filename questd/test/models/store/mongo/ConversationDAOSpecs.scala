

package models.store.mongo

import models.domain.chat.{Participant, Conversation}
import org.specs2.mutable._
import play.api.test._

//noinspection ZeroIndexToHead
class ConversationDAOSpecs extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  "Mongo Conversation DAO" should {
    "Search conversation by participant id" in new WithApplication(appWithTestDatabase) {
      db.culture.clear()

      val parts: List[Participant] = List("1", "2", "3").map(Participant(_))

      val convers: List[Conversation] = List(
        Conversation(participants = List(parts(0), parts(1))),
        Conversation(participants = List(parts(0), parts(2))),
        Conversation(participants = List(parts(1), parts(2)))
      )

      convers.foreach(db.conversation.create)

      val convs = db.conversation.findByParticipant(parts(1).userId).toList

      convs must beEqualTo(List(convers(0), convers(2)))
    }
  }

}

