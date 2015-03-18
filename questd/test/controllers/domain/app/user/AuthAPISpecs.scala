package controllers.domain.app.user

import controllers.domain._
import controllers.sn.client.User
import models.domain._
import models.store
import org.mockito.Matchers.{eq => mockEq}

class AuthAPISpecs extends BaseAPISpecs {

  private def userFBStub = {
    val fbid = "fbid"

    val userfb = mock[User]
    userfb.snId returns fbid
    userfb.invitations returns List.empty

    userfb
  }

  "Auth API" should {

    "Register user with new FB id" in context {

      val countryName = "country_name"
      val userfb = userFBStub

      val u = Some(User(
        id = "userid",
        auth = AuthInfo(snids = Map("FB" -> userfb.snId)),
        demo = UserDemographics(cultureId = Some(countryName)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(countryName))))))

      db.user.readBySNid("FB", userfb.snId) returns None thenReturns u
      db.user.levelUp(anyString, anyInt) returns u
      db.user.setNextLevelRatingAndRights(
        anyString,
        anyInt,
        any) returns u

      db.culture.findByCountry(countryName) returns Some(Culture(id = countryName, name = countryName))
      quest.allWithParams(any, any, any, any, any, any, any, any, any) returns List.empty.iterator
      solution.allWithParams(any, any, any, any, any, any, any, any, any, any, any) returns List.empty.iterator
      battle.allWithParams(any, any, any, any, any, any, any, any, any, any) returns List.empty.iterator
      user.setTimeLinePopulationTime(any, any) returns u

      val rv = api.login(LoginRequest("FB", userfb))

      there were atLeast(1)(quest).allWithParams(any, any, any, any, any, any, any, any, any)
      there were atLeast(1)(solution).allWithParams(any, any, any, any, any, any, any, any, any, any, any)
      there were atLeast(1)(battle).allWithParams(any, any, any, any, any, any, any, any, any, any)
      // Update allowed.
      there were two(user).readBySNid("FB", userfb.snId)
      there were one(user).create(any)

      rv must beAnInstanceOf[OkApiResult[LoginResult]]
      rv.body must beSome[LoginResult]
    }

    "Login existing user with new FB id" in context {

      val countryName = "country_name"
      val userfb = userFBStub

      val u = Some(User(
        id = "userid",
        auth = AuthInfo(snids = Map("FB" -> userfb.snId)),
        demo = UserDemographics(cultureId = Some(countryName)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(countryName))))))

      db.user.readBySNid("FB", userfb.snId) returns u
      db.culture.findByCountry(countryName) returns Some(Culture(id = countryName, name = countryName))

      val rv = api.login(LoginRequest("FB", userfb))

      there was one(user).readBySNid("FB", userfb.snId)

      rv must beAnInstanceOf[OkApiResult[LoginResult]]
      rv.body must beSome[LoginResult]
    }

    "Behaves well with DB exception" in context {
      db.user.readBySNid(anyString, anyString) throws new store.DatabaseException(silent = true)

      val userfb = userFBStub

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

      val userid = "uid"
      val currentCulture = "country_name_current"
      val actualCulture = "country_name_actual"
      val userfb = userFBStub

      val u = Some(User(
        id = userid,
        auth = AuthInfo(snids = Map("FB" -> userfb.snId)),
        demo = UserDemographics(cultureId = Some(currentCulture)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(currentCulture))))))

      db.user.readBySNid("FB", userfb.snId) returns u
      db.culture.findByCountry(currentCulture) returns Some(Culture(id = actualCulture, name = actualCulture))
      db.user.updateCultureId(userid, actualCulture) returns u

      val rv = api.login(LoginRequest("FB", userfb))

      there was one(user).readBySNid("FB", userfb.snId)
      there was one(user).updateCultureId(userid, actualCulture)

      rv must beAnInstanceOf[OkApiResult[LoginResult]]
      rv.body must beSome[LoginResult]
    }

    "Create culture if it's missing" in context {
      val userid = "uid"
      val currentCulture = "country_name_current"

      val userfb = userFBStub

      val u = Some(User(
        id = userid,
        auth = AuthInfo(snids = Map("FB" -> userfb.snId)),
        demo = UserDemographics(cultureId = Some(currentCulture)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(currentCulture))))))


      userfb.snId returns userfb.snId
      db.user.readBySNid("FB", userfb.snId) returns u
      db.culture.findByCountry(currentCulture) returns None

      db.user.updateCultureId(userid, currentCulture) returns u

      val rv = api.login(LoginRequest("FB", userfb))

      there was one(user).readBySNid("FB", userfb.snId)
      there was one(user).updateCultureId(mockEq(userid), any)
      there was one(culture).create(any)

      rv must beAnInstanceOf[OkApiResult[LoginResult]]
      rv.body must beSome[LoginResult]
    }
  }
}


