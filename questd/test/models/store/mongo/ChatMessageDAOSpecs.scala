

package models.store.mongo

import java.util.Date

import models.domain.chat.ChatMessage
import play.api.test.WithApplication

//noinspection ZeroIndexToHead
class ChatMessageDAOSpecs extends BaseDAOSpecs {

  "Mongo Chat Message DAO" should {
    "Dive me chat messages ordered by date and starting from particular one" in new WithApplication(appWithTestDatabase) {
      db.chat.clear()
      val conversationId = "conv"

      private val chats = (1 to 10).map(
        n =>
          ChatMessage(
            senderId = "s",
            conversationId = conversationId,
            creationDate = new Date(n),
            message = ""))
      chats.foreach(db.chat.create)

      val rv = db.chat.getForConversation(conversationId, new Date(5))

      rv.toList must beEqualTo(chats.drop(4))
    }
  }
}

