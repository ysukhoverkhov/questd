package controllers.domain.libs.facebook

import scala.language.implicitConversions
import com.restfb._

trait FacebookComponent {

  val fb: Facebook

  class Facebook {
    val FacebookClient = (x: String) => new FacebookClientRepeater(new DefaultFacebookClient(x))
    implicit def repeaterToClient(r: FacebookClientRepeater): FacebookClient = r.client

  }

}

