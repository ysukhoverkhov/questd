package logic.user

import controllers.domain.app.user.CreateConversationCode
import logic._
import models.domain.user.User

/**
 * All battle challenges related logic.
 */
trait Conversations { this: UserLogic =>

  def canConversateWith(peer: User): CreateConversationCode.Value = {
    import CreateConversationCode._

    if (user.banned.contains(peer.id)) {
      PeerBanned
    } else if (peer.banned.contains(user.id)) {
      UserBanned
    } else {
      OK
    }
  }
}
