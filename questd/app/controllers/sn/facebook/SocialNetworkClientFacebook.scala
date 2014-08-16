package controllers.sn.facebook

import controllers.sn.client.SocialNetworkClient
import scala.language.implicitConversions
import com.restfb._
import controllers.sn.client.SNUser
import com.restfb.exception._
import play.Logger
import controllers.sn.exception.AuthException
import controllers.sn.exception.NetworkException

private[sn] class SocialNetworkClientFacebook extends SocialNetworkClient {
  private val facebookClient = (x: String) => new FacebookClientRepeater(new DefaultFacebookClient(x))
  implicit def repeaterToClient(r: FacebookClientRepeater): FacebookClient = r.client

  private def handleExceptions[T](f: => T): T = try {
    f
  } catch {
    case ex: FacebookOAuthException => {
      Logger.debug("Facebook auth failed")
      throw new AuthException
    }
    case ex: FacebookNetworkException => {
      Logger.debug("Unable to connect to facebook")
      throw new NetworkException
    }
  }

  /// Get user of social network.
  def fetchUserByToken(token: String): SNUser = handleExceptions {

    val client = facebookClient(token)
    return SNUserFacebook(client.fetchObject("me", classOf[com.restfb.types.User]))

  }

  /// Get all social networks friends.
  def fetchFriendsByToken(token: String): List[SNUser] = handleExceptions {
    import collection.JavaConversions._
    facebookClient(token).fetchConnection("me/friends", classOf[com.restfb.types.User]).getData().toList.map(SNUserFacebook(_))
  }

}

private[sn] object SocialNetworkClientFacebook {
  def apply(): SocialNetworkClientFacebook = new SocialNetworkClientFacebook()
}

