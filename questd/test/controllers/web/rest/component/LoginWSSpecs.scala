package controllers.web.rest.component

import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.user._
import controllers.services.socialnetworks.client.{SocialNetworkClient, User => SNUser}
import controllers.services.socialnetworks.component.SocialNetworkComponent
import controllers.services.socialnetworks.exception._
import controllers.web.rest.component.LoginWSImplTypes.WSLoginRequest
import models.domain.user.User
import models.store._
import org.specs2.mock.Mockito
import org.specs2.mutable._
import play.api.libs.json._
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

class LoginWSSpecs extends Specification
  with RandomComponent
  with WSComponent
  with SocialNetworkComponent
  with DatabaseComponent
  with DomainAPIComponent
  with Mockito {

  isolated

  val db = mock[Database]
  val api = mock[DomainAPI]
  val sn = mock[SocialNetwork]
  val rand = mock[Random]
  lazy val ws = new WS

  "Login Web Service" should {
    "Login user with correct FB token" in new WithApplication {

      val facebookToken = "Facebook token"
      val sessid = "sess id"
      val userId = "user Id"

      val user = mock[SNUser]
      val fbsn = mock[SocialNetworkClient]
      sn.clientForName("FB") returns fbsn
      fbsn.isValidUserToken(facebookToken) returns true
      fbsn.fetchUserByToken(facebookToken) returns user
      api.login(LoginRequest("FB", user)) returns OkApiResult(LoginResult  (sessid, userId))

      val data = AnyContentAsJson(Json.parse(controllers.web.helpers.Json.write[WSLoginRequest](WSLoginRequest("FB", facebookToken, 1))))

      val fakeRequest = FakeRequest(
        Helpers.POST,
        "",
        FakeHeaders(),
        data)

      val r: Future[Result] = ws.login()(fakeRequest)

      status(r) must equalTo(OK)
      contentType(r) must beSome("application/json")
      contentAsString(r) must contain(sessid)
      session(r).get(controllers.web.rest.component.SecurityWSImplTypes.SessionIdKey) must beSome
    }

    "Do not login user with incorrect FB token" in new WithApplication {

      val facebookToken = "Facebook token"

      val user = mock[User]
      val fbsn = mock[SocialNetworkClient]
      sn.clientForName("FB") returns fbsn
      fbsn.isValidUserToken(facebookToken) returns true
      fbsn.fetchUserByToken(facebookToken) throws new AuthException()


      val data = AnyContentAsJson(Json.parse(controllers.web.helpers.Json.write[WSLoginRequest](WSLoginRequest("FB", facebookToken, 1))))

      val fakeRequest = FakeRequest(
        Helpers.POST,
        "",
        FakeHeaders(),
        data)

      val r = ws.login()(fakeRequest)

      status(r) must equalTo(UNAUTHORIZED)
    }

    "Report about unaddressable FB in case of unavailable Facebook" in new WithApplication {

      val facebookToken = "Facebook token"

      val user = mock[User]
      val fbsn = mock[SocialNetworkClient]
      sn.clientForName("FB") returns fbsn
      fbsn.isValidUserToken(facebookToken) returns true
      fbsn.fetchUserByToken(facebookToken) throws new NetworkException()

      val data = AnyContentAsJson(Json.parse(controllers.web.helpers.Json.write[WSLoginRequest](WSLoginRequest("FB", facebookToken, 1))))

      val fakeRequest = FakeRequest(
        Helpers.POST,
        "",
        FakeHeaders(),
        data)

      val r = ws.login()(fakeRequest)

      status(r) must equalTo(SERVICE_UNAVAILABLE)
    }

    "Report about token from another app or outdated token" in new WithApplication {

      val facebookToken = "Facebook token"

      val user = mock[SNUser]
      val fbsn = mock[SocialNetworkClient]
      sn.clientForName("FB") returns fbsn
      fbsn.isValidUserToken(facebookToken) returns false
      fbsn.fetchUserByToken(facebookToken) returns user

      val data = AnyContentAsJson(Json.parse(controllers.web.helpers.Json.write[WSLoginRequest](WSLoginRequest("FB", facebookToken, 1))))

      val fakeRequest = FakeRequest(
        Helpers.POST,
        "",
        FakeHeaders(),
        data)

      val r = ws.login()(fakeRequest)

      status(r) must equalTo(BAD_REQUEST)
    }


    "Workout incorrect SN name" in new WithApplication {

      val facebookToken = "Facebook token"

      val user = mock[User]
      val fbsn = mock[SocialNetworkClient]
      sn.clientForName("FB") returns fbsn
      fbsn.fetchUserByToken(facebookToken) throws new SocialNetworkClientNotFound()

      val data = AnyContentAsJson(Json.parse(controllers.web.helpers.Json.write[WSLoginRequest](WSLoginRequest("FB", facebookToken, 1))))

      val fakeRequest = FakeRequest(
        Helpers.POST,
        "",
        FakeHeaders(),
        data)

      val r = ws.login()(fakeRequest)

      status(r) must equalTo(BAD_REQUEST)
    }

  }
}

