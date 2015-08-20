

package models.store.mongo

import java.util.Date

import models.domain.chat.ChatMessage
import org.specs2.mutable._
import play.api.test._

//noinspection ZeroIndexToHead
class ChatMessageDAOSpecs extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  "Mongo Chat Message DAO" should {
    "Dive me chat messages ordered by date and starting from particular one" in new WithApplication(appWithTestDatabase) {
      db.chat.clear()
      val conversationId = "conv"

      private val chats = (1 to 10).map(
        n =>
          ChatMessage(
            sender = "s",
            conversationId = conversationId,
            creationDate = new Date(n)))
      chats.foreach(db.chat.create)

      val rv = db.chat.getForConversation(conversationId, new Date(5))

      rv.toList must beEqualTo(chats.drop(4))
    }
  }

}

