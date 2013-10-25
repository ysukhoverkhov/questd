package controllers.helpers

import scala.language.implicitConversions
import com.restfb._


package object facebook {
  
  type UserFB = com.restfb.types.User
  
  val FacebookClient = (x: String) => new FacebookClientRepeater(new DefaultFacebookClient(x))
  implicit def repeaterToClient(r: FacebookClientRepeater): FacebookClient = r.client

}

