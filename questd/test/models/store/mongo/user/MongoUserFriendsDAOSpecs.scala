package models.store.mongo.user

import models.domain.user.User
import models.domain.user.friends.{ReferralStatus, FriendshipStatus, Friendship}
import models.store.mongo.BaseDAOSpecs
import play.api.test.WithApplication
import testhelpers.domainstubs._

/**
 * MongoUserFriendsDAO specs
 */
trait MongoUserFriendsDAOSpecs { this: BaseDAOSpecs =>
  "Mongo User DAO" should {
    "Setting friendship works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val friend = createUserStub()
      val fs = Friendship(friend.id, FriendshipStatus.Invited)

      db.user.create(user)
      db.user.addFriendship(user.id, fs)
      db.user.updateFriendship(
        user.id, friend.id, Some(FriendshipStatus.Accepted.toString), Some(ReferralStatus.ReferredBy.toString))

      val ou2 = db.user.readById(user.id)
      ou2 must beSome[User]
      val u = ou2.get
      u.friends.head.status must beEqualTo(FriendshipStatus.Accepted)
      u.friends.head.referralStatus must beEqualTo(ReferralStatus.ReferredBy)
    }
  }
}
