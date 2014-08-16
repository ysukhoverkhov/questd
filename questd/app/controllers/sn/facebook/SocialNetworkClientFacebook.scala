package controllers.sn.facebook

import controllers.sn.client.SocialNetworkClient
import scala.language.implicitConversions
import com.restfb._
import controllers.sn.client.SNUser

private[sn] class SocialNetworkClientFacebook extends SocialNetworkClient {
  private val facebookClient = (x: String) => new FacebookClientRepeater(new DefaultFacebookClient(x))
  implicit def repeaterToClient(r: FacebookClientRepeater): FacebookClient = r.client

  // TODO: perhaps these two functions are useless.
  private def fetchConnection[T](token: String, obj: String, objectType: Class[T], parameters: Parameter*): com.restfb.Connection[T] = {
    facebookClient(token).fetchConnection(obj, objectType, parameters: _*)
  }

  def fetchUserByToken(token: String): SNUser = {
    val client = facebookClient(token)
    return SNUserFacebook(client.fetchObject("me", classOf[com.restfb.types.User]))
  }

  /// Get all social networks friends.
  def fetchFriendsByToken(token: String): List[SNUser] = {
    import collection.JavaConversions._
    facebookClient(token).fetchConnection("me/friends", classOf[com.restfb.types.User]).getData().toList.map(SNUserFacebook(_))
  }

}

private[sn] object SocialNetworkClientFacebook {
  def apply(): SocialNetworkClientFacebook = new SocialNetworkClientFacebook()
}

