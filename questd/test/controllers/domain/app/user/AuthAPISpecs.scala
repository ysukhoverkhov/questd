package controllers.domain.app.user

import controllers.domain.{InternalErrorApiResult, NotAuthorisedApiResult, _}
import controllers.sn.client.User
import models.domain._
import models.store._
import org.mockito.Matchers


class AuthAPISpecs extends BaseAPISpecs {

  "Auth API" should {

    "Register user with new FB id" in context {

      val fbid = "fbid"
      val countryName = "country_name"

      val userfb = mock[User]
      userfb.snId returns fbid

      val u = Some(User(
        id = "userid",
        auth = AuthInfo(snids = Map("FB" -> fbid)),
        demo = UserDemographics(cultureId = Some(countryName)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(countryName))))))

      db.user.readBySNid("FB", fbid) returns None thenReturns u
      db.user.levelup(anyString, anyInt) returns u
      db.user.setNextLevelRatingAndRights(
        anyString,
        anyInt,
        any) returns u
      db.culture.findByCountry(countryName) returns Some(Culture(id = countryName, name = countryName))

      val rv = api.login(LoginRequest("FB", userfb))

      // Update allowed.
      there were two(user).readBySNid("FB", fbid)
//      there were one(user).create(any)
//      there were one(user).update(any)

      rv must beAnInstanceOf[OkApiResult[LoginResult]]
      rv.body must beSome[LoginResult]
    }

    "Login existing user with new FB id" in context {

      val fbid = "fbid"
      val countryName = "country_name"

      val u = Some(User(
        id = "userid",
        auth = AuthInfo(snids = Map("FB" -> fbid)),
        demo = UserDemographics(cultureId = Some(countryName)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(countryName))))))

      val userfb = mock[User]

      userfb.snId returns fbid
      db.user.readBySNid("FB", fbid) returns u
      db.culture.findByCountry(countryName) returns Some(Culture(id = countryName, name = countryName))

      val rv = api.login(LoginRequest("FB", userfb))

      there was one(user).readBySNid("FB", fbid)

      rv must beAnInstanceOf[OkApiResult[LoginResult]]
      rv.body must beSome[LoginResult]
    }

    "Behaves well with DB exception" in context {

      db.user.readBySNid(anyString, anyString) throws new DatabaseException("Test exception")

      val userfb = mock[User]
      userfb.snId returns "1"

      val rv = api.login(LoginRequest("FB", userfb))

      there was one(user).readBySNid(anyString, anyString)

      rv must beAnInstanceOf[InternalErrorApiResult[LoginResult]]
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

    "Update culture on login" in context {
      val fbid = "fbid"
      val userid = "uid"
      val currentCulture = "country_name_current"
      val actualCulture = "country_name_actual"

      val u = Some(User(
        id = userid,
        auth = AuthInfo(snids = Map("FB" -> fbid)),
        demo = UserDemographics(cultureId = Some(currentCulture)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(currentCulture))))))

      val userfb = mock[User]

      userfb.snId returns fbid
      db.user.readBySNid("FB", fbid) returns u
      db.culture.findByCountry(currentCulture) returns Some(Culture(id = actualCulture, name = actualCulture))
      db.user.updateCultureId(userid, actualCulture) returns u

      val rv = api.login(LoginRequest("FB", userfb))

      there was one(user).readBySNid("FB", fbid)
      there was one(user).updateCultureId(userid, actualCulture)

      rv must beAnInstanceOf[OkApiResult[LoginResult]]
      rv.body must beSome[LoginResult]
    }

    "Create culture if it's missing" in context {
      val fbid = "fbid"
      val userid = "uid"
      val currentCulture = "country_name_current"

      val u = Some(User(
        id = userid,
        auth = AuthInfo(snids = Map("FB" -> fbid)),
        demo = UserDemographics(cultureId = Some(currentCulture)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(currentCulture))))))

      val userfb = mock[User]

      userfb.snId returns fbid
      db.user.readBySNid("FB", fbid) returns u
      db.culture.findByCountry(currentCulture) returns None

      db.user.updateCultureId(userid, currentCulture) returns u

      val rv = api.login(LoginRequest("FB", userfb))

      there was one(user).readBySNid("FB", fbid)
      there was one(user).updateCultureId(Matchers.eq(userid), any)
      there was one(culture).create(any)

      rv must beAnInstanceOf[OkApiResult[LoginResult]]
      rv.body must beSome[LoginResult]
    }
  }
}


