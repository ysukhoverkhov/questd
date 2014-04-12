package controllers.domain.libs.facebook

import scala.language.implicitConversions
import com.restfb._

trait FacebookComponent {

  protected val fb: Facebook

  class Facebook {
    val FacebookClient = (x: String) => new FacebookClientRepeater(new DefaultFacebookClient(x))
    implicit def repeaterToClient(r: FacebookClientRepeater): FacebookClient = r.client

    def fetchObject[T](token:String, obj: String, objectType: Class[T], parameters: Parameter*): T = {
      FacebookClient(token).fetchObject(obj, objectType, parameters: _*)
    }
    
  }

}

