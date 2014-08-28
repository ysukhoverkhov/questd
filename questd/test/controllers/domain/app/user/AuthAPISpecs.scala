package controllers.domain.app.user

import controllers.domain.{InternalErrorApiResult, NotAuthorisedApiResult, _}
import controllers.sn.client.SNUser
import models.domain._
import models.store._


class AuthAPISpecs extends BaseAPISpecs {

  "Auth API" should {

    "Register user with new FB id" in context {

      val fbid = "fbid"
      val candc = "country_name"

      val userfb = mock[SNUser]
      userfb.snId returns fbid

      val u = Some(User("", AuthInfo(snids = Map("FB" -> fbid)), demo = UserDemographics(cultureId = candc)))

      db.user.readBySNid("FB", fbid) returns None thenReturns u
      db.user.levelup(anyString, anyInt) returns Some(User("", AuthInfo(snids = Map("FB" -> fbid))))
      db.user.setNextLevelRatingAndRights(
        anyString,
        anyInt,
        any) returns Some(User("", AuthInfo(snids = Map("FB" -> fbid))))
      db.culture.findByCountry("") returns Some(Culture(id = candc, name = candc))

      val rv = api.login(LoginRequest("FB", userfb))

      // Update allowed.
      there were two(user).readBySNid("FB", fbid)
      //      there were one(user).create(u.get)
      //      there were one(user).update(u.get)

      rv must beAnInstanceOf[OkApiResult[LoginResult]]
      rv.body must beSome[LoginResult]
    }

    "Login existing user with new FB id" in context {

      val fbid = "fbid"
      val candc = "country_name"

      val userfb = mock[SNUser]

      userfb.snId returns fbid
      db.user.readBySNid("FB", fbid) returns Some(User(
        "",
        auth = AuthInfo(snids = Map("FB" -> fbid)),
        demo = UserDemographics(cultureId = candc)))
      db.culture.findByCountry("") returns Some(Culture(id = candc, name = candc))

      val rv = api.login(LoginRequest("FB", userfb))

      there was one(user).readBySNid("FB", fbid)

      rv must beAnInstanceOf[OkApiResult[LoginResult]]
      rv.body must beSome[LoginResult]
    }

    "Behaves well with DB exception" in context {

      db.user.readBySNid(anyString, anyString) throws new DatabaseException("Test exception")

      val userfb = mock[SNUser]
      userfb.snId returns "1"

      val rv = api.login(LoginRequest("FB", userfb))

      there was one(user).readBySNid(anyString, anyString)

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


