package logic.user

import controllers.domain.app.protocol.ProfileModificationResult._
import logic._
import models.domain.user.User

/**
 * All battle challenges related logic.
 */
trait Conversations { this: UserLogic =>

  def canCreateConversationWith(peer: User) = {
    if (user.banned.contains(peer.id)) {
      InvalidState
    } else if (peer.banned.contains(user.id)) {
      InvalidState
    } else {
      OK
    }
  }
}
