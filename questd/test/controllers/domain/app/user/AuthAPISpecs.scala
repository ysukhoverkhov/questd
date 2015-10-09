package controllers.domain.app.user

import controllers.domain._
import controllers.services.socialnetworks.client.User
import models.domain.culture.Culture
import models.domain.user._
import models.domain.user.auth.{AuthInfo, LoginMethod}
import models.domain.user.demo.UserDemographics
import models.domain.user.profile.{Bio, Profile, PublicProfile}
import models.store
import org.mockito.Matchers.{eq => mockEq}

class AuthAPISpecs extends BaseAPISpecs {

  private def userFBStub = {
    val fbid = "fbid"

    val userfb = mock[User]
    userfb.snId returns fbid
    userfb.invitations returns List.empty
    userfb.idsInOtherApps returns List.empty

    userfb
  }

  "Auth API" should {

    "Register user with new FB id" in context {

      val countryName = "country_name"
      val userfb = userFBStub

      val u = Some(User(
        id = "userid",
        auth = AuthInfo(loginMethods = List(LoginMethod("FB", userfb.snId))),
        demo = UserDemographics(cultureId = Some(countryName)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(countryName))))))

      db.user.updateSessionId(any, any) returns u
      db.user.readBySNid("FB", userfb.snId) returns None thenReturns u
      db.user.levelUp(anyString, anyInt) returns u
      db.user.setNextLevelRatingAndRights(
        anyString,
        anyInt,
        any) returns u
      db.culture.findByCountry(countryName) returns Some(Culture(id = countryName, name = countryName))
      doReturn(OkApiResult(NotifySNFriendsAboutLoginResult(u.get))).when(api).notifySNFriendsAboutLogin(any)

      val rv = api.login(LoginRequest("FB", userfb))

      // Update allowed.
      there was one(user).readBySNid("FB", userfb.snId)
      there was one(user).create(any)
      there was one(api).notifySNFriendsAboutLogin(any)

      rv must beAnInstanceOf[OkApiResult[LoginResult]]
      rv.body must beSome[LoginResult]
    }

    "Login existing user with new FB id" in context {

      val countryName = "country_name"
      val userfb = userFBStub

      val u = Some(User(
        id = "userid",
        auth = AuthInfo(loginMethods = List(LoginMethod("FB", userfb.snId))),
        demo = UserDemographics(cultureId = Some(countryName)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(countryName))))))

      db.user.updateSessionId(any, any) returns u
      db.user.readBySNid("FB", userfb.snId) returns u
      db.culture.findByCountry(countryName) returns Some(Culture(id = countryName, name = countryName))
      doReturn(OkApiResult(NotifySNFriendsAboutLoginResult(u.get))).when(api).notifySNFriendsAboutLogin(any)

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

      val rv = api.getUser(GetUserRequest(sessionId = Some(sesid)))

      rv must beAnInstanceOf[OkApiResult[GetUserResult]]
      rv.body must beSome[GetUserResult] and beSome.which((u: GetUserResult) =>
        u.user.get.auth.session.contains(sesid))
    }

    "Do not return none existing user" in context {
      val sesid = "session id"

      db.user.readBySessionId(sesid) returns None

      val rv = api.getUser(GetUserRequest(sessionId = Some(sesid)))

      rv must beAnInstanceOf[OkApiResult[GetUserResult]]
      rv.body must beSome[GetUserResult] and beSome.which((u: GetUserResult) =>
        u.code == UserResultCode.NotFound)
    }

    "Update culture on login" in context {

      val userid = "uid"
      val currentCulture = "country_name_current"
      val actualCulture = "country_name_actual"
      val userfb = userFBStub

      val u = Some(User(
        id = userid,
        auth = AuthInfo(loginMethods = List(LoginMethod("FB", userfb.snId))),
        demo = UserDemographics(cultureId = Some(currentCulture)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(currentCulture))))))

      db.user.updateSessionId(any, any) returns u
      db.user.readBySNid("FB", userfb.snId) returns u
      db.culture.findByCountry(currentCulture) returns Some(Culture(id = actualCulture, name = actualCulture))
      db.user.updateCultureId(userid, actualCulture) returns u
      doReturn(OkApiResult(NotifySNFriendsAboutLoginResult(u.get))).when(api).notifySNFriendsAboutLogin(any)

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
        auth = AuthInfo(loginMethods = List(LoginMethod("FB", userfb.snId))),
        demo = UserDemographics(cultureId = Some(currentCulture)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(currentCulture))))))


      userfb.snId returns userfb.snId
      db.user.updateSessionId(any, any) returns u
      db.user.readBySNid("FB", userfb.snId) returns u
      db.culture.findByCountry(currentCulture) returns None
      db.user.updateCultureId(userid, currentCulture) returns u
      doReturn(OkApiResult(NotifySNFriendsAboutLoginResult(u.get))).when(api).notifySNFriendsAboutLogin(any)

      val rv = api.login(LoginRequest("FB", userfb))

      there was one(user).readBySNid("FB", userfb.snId)
      there was one(user).updateCultureId(mockEq(userid), any)
      there was one(culture).create(any)

      rv must beAnInstanceOf[OkApiResult[LoginResult]]
      rv.body must beSome[LoginResult]
    }

    "Creates friendship if referrer id is present" in context {

      val countryName = "country_name"
      val userfb = userFBStub
      val referrerId = "referrerId"
      val contentId = "contentId"

      val u = Some(User(
        id = "userid",
        auth = AuthInfo(loginMethods = List(LoginMethod("FB", userfb.snId))),
        demo = UserDemographics(cultureId = Some(countryName)),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              country = Some(countryName))))))

      db.user.updateSessionId(any, any) returns u
      db.user.readBySNid("FB", userfb.snId) returns None thenReturns u
      db.user.levelUp(anyString, anyInt) returns u
      db.user.setNextLevelRatingAndRights(
        anyString,
        anyInt,
        any) returns u
      db.culture.findByCountry(countryName) returns Some(Culture(id = countryName, name = countryName))
      doReturn(OkApiResult(CreateFriendshipResult(u.get))).when(api).createFriendship(any)
      doReturn(OkApiResult(NotifySNFriendsAboutLoginResult(u.get))).when(api).notifySNFriendsAboutLogin(any)

      val rv = api.login(
        LoginRequest(
          "FB",
          userfb,
          referrerId = Some(referrerId),
          invitedWithContentId = Some(contentId)))

      rv must beAnInstanceOf[OkApiResult[LoginResult]]

      // Update allowed.
      there was one(user).readBySNid("FB", userfb.snId)
      there was one(user).create(any)
      there was one(api).createFriendship(any)
    }
  }
}


