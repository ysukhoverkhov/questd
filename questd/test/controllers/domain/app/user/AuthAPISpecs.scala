package controllers.domain.app.user

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import controllers.domain._
import controllers.domain.app.user._
import models.store._
import models.domain._
import models.store.mongo._
import controllers.domain.libs.facebook.UserFB
import controllers.domain.InternalErrorApiResult
import controllers.domain.NotAuthorisedApiResult


class AuthAPISpecs extends BaseAPISpecs {

  "Auth API" should {

    "Register user with new FB id" in context {

      val fbid = "fbid"
      val userfb = mock[UserFB]
      userfb.getId returns fbid
        
      db.user.readByFBid(anyString) returns None thenReturns Some(User("", AuthInfo(fbid = Some(fbid))))
      db.user.levelup(anyString, anyInt) returns Some(User("", AuthInfo(fbid = Some(fbid))))
      db.user.setNextLevelRatingAndRights(
        anyString,
        anyInt,
        any) returns Some(User("", AuthInfo(fbid = Some(fbid))))
      
      val rv = api.loginfb(LoginFBRequest(userfb, ""))

      // Update allowed.
      there was one(user).readByFBid(fbid) andThen 
        one(user).create(any[User]) andThen
        one(user).readByFBid(fbid) andThen
        one(user).update(any[User])

      rv must beAnInstanceOf[OkApiResult[LoginFBResult]]
      rv.body must beSome[LoginFBResult]
    }

    "Login existing user with new FB id" in context {

      val fbid = "fbid"
      val userfb = mock[UserFB]
      userfb.getId returns fbid

      db.user.readByFBid(anyString) returns Some(User("", AuthInfo(fbid = Some(fbid))))

      val rv = api.loginfb(LoginFBRequest(userfb, ""))

      there was one(user).readByFBid(fbid) andThen one(user).create(any[User])

      rv must beAnInstanceOf[OkApiResult[LoginFBResult]]
      rv.body must beSome[LoginFBResult]
    }

    "Behaves well with DB exception" in context {

      db.user.readByFBid(anyString) throws new DatabaseException("Test exception")

      val userfb = mock[UserFB]
      userfb.getId returns "1"

      val rv = api.loginfb(LoginFBRequest(userfb, ""))

      there was one(user).readByFBid(anyString)

      rv must beAnInstanceOf[InternalErrorApiResult]
      rv.body must beNone
    }

    "Return logged in user" in context {

      val sesid = "session id"

      db.user.readBySessionId(sesid) returns Some(User("", AuthInfo(session = Some(sesid))))

      val rv = api.getUser(UserRequest(sessionId = Some(sesid)))

      rv must beAnInstanceOf[OkApiResult[UserResult]]
      rv.body must beSome[UserResult] and beSome.which((u: UserResult) =>
        u.user.auth.session == Some(sesid))

    }

    "Do not return none existing user" in context {
      val sesid = "session id"

      db.user.readBySessionId(sesid) returns None

      val rv = api.getUser(UserRequest(sessionId = Some(sesid)))

      rv must beAnInstanceOf[NotAuthorisedApiResult]
      rv.body must beNone
    }
  }

}


