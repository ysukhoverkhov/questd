package models.store.mongo.user

import models.domain.user.User
import models.domain.user.auth.{LoginMethod, AuthInfo}
import models.store.mongo.BaseDAOSpecs
import play.api.test.WithApplication

/**
 * MongoUserFetchDAO specs
 */
trait MongoUserFetchDAOSpecs { this: BaseDAOSpecs =>

  "Mongo User DAO" should {
    "Find user by FB id" in new WithApplication(appWithTestDatabase) {
      val fbid = "idid_fbid"
      val user_id = "session name"
      db.user.create(User(user_id, AuthInfo(loginMethods = List(LoginMethod("FB", fbid)))))
      val u = db.user.readBySNid("FB", fbid)

      u must beSome
      u must beSome.which((u: User) => u.id == user_id)
    }

    "Find user by session id" in new WithApplication(appWithTestDatabase) {
      val sessid = "idid"
      val testsess = "session name"
      db.user.create(User(testsess, AuthInfo(session = Some(sessid))))
      val u = db.user.readBySessionId(sessid)
      u must beSome.which((u: User) => u.id.toString == testsess) and
        beSome.which((u: User) => u.auth.loginMethods == List.empty) and
        beSome.which((u: User) => u.auth.session.contains(sessid))
    }
  }
}
