package controllers.web.rest.component

import scala.concurrent.Future
import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.mock.Mockito
import org.junit.runner._
import play.Logger
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json._
import components.componentregistry._
import controllers.domain._
import controllers.domain.app.user._
import controllers.web.rest.component._
import models.store._
import models.domain._
import models.store.mongo._
import com.restfb.exception._
import controllers.web.rest.protocol._
import play.api.mvc._
import components.random.RandomComponent
import controllers.sn.component.SocialNetworkComponent
import controllers.sn.client._
import controllers.sn.exception._

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

      val user = mock[SNUser]
      val fbsn = mock[SocialNetworkClient]
      sn.clientForName("FB") returns fbsn 
      fbsn.fetchUserByToken(facebookToken) returns user
      api.login(LoginRequest("FB", user)) returns OkApiResult(LoginResult(sessid))

      val data = AnyContentAsJson(Json.parse(controllers.web.rest.component.helpers.Json.write[WSLoginRequest](WSLoginRequest("FB", facebookToken, 1))))
      
      val fakeRequest = FakeRequest(
        Helpers.POST,
        "",
        FakeHeaders(),
        data)

      val r: Future[SimpleResult] = ws.login()(fakeRequest)

      status(r) must equalTo(OK)
      contentType(r) must beSome("application/json")
      contentAsString(r) must contain(sessid)
      session(r).get(controllers.web.rest.component.SecurityWSImpl.SessionIdKey) must beSome
    }

    "Do not login user with incorrect FB token" in new WithApplication {

      val facebookToken = "Facebook token"

      val user = mock[SNUser]
      val fbsn = mock[SocialNetworkClient]
      sn.clientForName("FB") returns fbsn 
      fbsn.fetchUserByToken(facebookToken) throws new AuthException()
      

      val data = AnyContentAsJson(Json.parse(controllers.web.rest.component.helpers.Json.write[WSLoginRequest](WSLoginRequest("FB", facebookToken, 1))))

      val fakeRequest = FakeRequest(
        Helpers.POST,
        "",
        FakeHeaders(),
        data)

      val r = ws.login()(fakeRequest)

      status(r) must equalTo(UNAUTHORIZED)
    }

    "Report about unaccessable FB in case of unavailable Facebook" in new WithApplication {

      val facebookToken = "Facebook token"

      val user = mock[SNUser]
      val fbsn = mock[SocialNetworkClient]
      sn.clientForName("FB") returns fbsn 
      fbsn.fetchUserByToken(facebookToken) throws new NetworkException()

      val data = AnyContentAsJson(Json.parse(controllers.web.rest.component.helpers.Json.write[WSLoginRequest](WSLoginRequest("FB", facebookToken, 1))))

      val fakeRequest = FakeRequest(
        Helpers.POST,
        "",
        FakeHeaders(),
        data)

      val r = ws.login()(fakeRequest)

      status(r) must equalTo(SERVICE_UNAVAILABLE)
    }

    "Workout incorect SN name" in new WithApplication {

      val facebookToken = "Facebook token"

      val user = mock[SNUser]
      val fbsn = mock[SocialNetworkClient]
      sn.clientForName("FB") returns fbsn 
      fbsn.fetchUserByToken(facebookToken) throws new SocialNetworkClientNotFound()

      val data = AnyContentAsJson(Json.parse(controllers.web.rest.component.helpers.Json.write[WSLoginRequest](WSLoginRequest("FB", facebookToken, 1))))

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

