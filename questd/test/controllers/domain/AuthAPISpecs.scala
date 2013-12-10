package controllers.domain

import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.mock.Mockito
import org.junit.runner._
import play.Logger
import play.api.test._
import play.api.test.Helpers._

import controllers.domain._
import controllers.domain.user._

import models.store._
import models.domain._
import models.store.mongo._
import models.store.dao.UserDAO


class AuthAPISpecs extends Specification
  with DatabaseComponent
  with DomainAPIComponent
  with Mockito {

  isolated

  val db = mock[Database]
  val user = mock[UserDAO]
  
  val api = new DomainAPI

  object context extends org.specs2.mutable.Before {
    def before =  db.user returns user
  }

  
  "Auth API" should {

    "Register user with new FB id" in context {

      val fbid = "fbid"
        
      db.user.readByFBid(anyString) returns None thenReturns Some(User("", AuthInfo(fbid = Some(fbid))))
      //    db.createUser(newUser)
      //    db.readUserByFBid(fbid) returns 
      //    db.updateUser(user.replaceSessionID(uuid))

      val rv = api.loginfb(LoginFBRequest(fbid))

      there was one(user).readByFBid(fbid) andThen 
        one(user).create(any[User]) andThen
        one(user).readByFBid(fbid) andThen
        one(user).update(any[User])

      rv must beAnInstanceOf[OkApiResult[LoginFBResult]]
      rv.body must beSome[LoginFBResult]
    }

    "Login existing user with new FB id" in context {

      val fbid = "fbid"

      db.user.readByFBid(anyString) returns Some(User("", AuthInfo(fbid = Some(fbid))))
      //    db.updateUser(user.replaceSessionID(uuid))

      val rv = api.loginfb(LoginFBRequest(fbid))

      there was one(user).readByFBid(fbid) andThen one(user).create(any[User])

      rv must beAnInstanceOf[OkApiResult[LoginFBResult]]
      rv.body must beSome[LoginFBResult]
    }

    "Behaves well with DB exception" in context {

      db.user.readByFBid(anyString) throws new DatabaseException("Test exception")

      val rv = api.loginfb(LoginFBRequest("any id"))

      there was one(user).readByFBid(anyString)

      rv must beAnInstanceOf[InternalErrorApiResult[LoginFBResult]]
      rv.body must beNone
    }

    //    case class UserParams(sessionID: SessionID)
    //case class UserResult(user: User)

    "Return logged in user" in context {

      val sesid = "session id"

      db.user.readBySessionID(sesid) returns Some(User("", AuthInfo(session = Some(sesid))))

      val rv = api.user(UserRequest(sessionID = Some(sesid)))

      rv must beAnInstanceOf[OkApiResult[UserResult]]
      rv.body must beSome[UserResult] and beSome.which((u: UserResult) =>
        u.user.auth.session == Some(sesid))

    }

    "Do not return none existing user" in context {
      val sesid = "session id"

      db.user.readBySessionID(sesid) returns None

      val rv = api.user(UserRequest(sessionID = Some(sesid)))

      rv must beAnInstanceOf[NotAuthorisedApiResult[UserResult]]
      rv.body must beNone
    }
  }

}


