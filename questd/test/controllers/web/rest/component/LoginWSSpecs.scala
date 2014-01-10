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
import controllers.domain.libs.facebook._
import controllers.web.rest.component._
import models.store._
import models.domain._
import models.store.mongo._
import com.restfb.exception._
import controllers.web.rest.protocol._
import play.api.mvc._

class LoginWSSpecs extends Specification
  with WSComponent
  with FacebookComponent
  with DatabaseComponent
  with DomainAPIComponent
  with Mockito {

  isolated

  val db = mock[Database]
  val api = mock[DomainAPI]
  val fb = mock[Facebook]
  lazy val ws = new WS

  "Login Web Service" should {
    "Login user with correct FB token" in new WithApplication {

      val facebookToken = "Facebook token"
      val sessid = "sess id"

      val user = mock[UserFB]
      fb.fetchObject(facebookToken, "me", classOf[UserFB]) returns user
      api.loginfb(LoginFBRequest(user)) returns OkApiResult(Some(LoginFBResult(sessid)))

//      import controllers.web.rest.component.helpers._

      val data = AnyContentAsText(controllers.web.rest.component.helpers.Json.write[WSLoginFBRequest](WSLoginFBRequest(facebookToken, 1)))

      val fakeRequest = FakeRequest[AnyContent](
        Helpers.POST,
        "",
        FakeHeaders(),
        data)

      val r: Future[SimpleResult] = ws.loginfb()(fakeRequest)

      status(r) must equalTo(OK)
      contentType(r) must beSome("application/json")
      contentAsString(r) must contain(sessid)
      session(r).get(controllers.web.rest.component.SecurityWSImpl.SessionIdKey) must beSome
    }

    "Do not login user with incorrect FB token" in new WithApplication {

      val facebookToken = "Facebook token"

      fb.fetchObject(facebookToken, "me", classOf[UserFB]) throws new FacebookOAuthException("", "", 1, 1)

      val data = AnyContentAsText(controllers.web.rest.component.helpers.Json.write[WSLoginFBRequest](WSLoginFBRequest(facebookToken, 1)))

      val fakeRequest = FakeRequest(
        Helpers.POST,
        "",
        FakeHeaders(),
        data)

      val r = ws.loginfb()(fakeRequest)

      status(r) must equalTo(UNAUTHORIZED)
    }

    "Report about unaccessable FB in case of unavailable Facebook" in new WithApplication {

      val facebookToken = "Facebook token"

      fb.fetchObject(facebookToken, "me", classOf[UserFB]) throws new FacebookNetworkException("", null, 1)

      val data = AnyContentAsText(controllers.web.rest.component.helpers.Json.write[WSLoginFBRequest](WSLoginFBRequest(facebookToken, 1)))

      val fakeRequest = FakeRequest(
        Helpers.POST,
        "",
        FakeHeaders(),
        data)

      val r = ws.loginfb()(fakeRequest)

      status(r) must equalTo(SERVICE_UNAVAILABLE)
    }

  }

}

