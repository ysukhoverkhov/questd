package controllers.sn.facebook

import controllers.sn.client.SocialNetworkClient
import scala.language.implicitConversions
import com.restfb._
import controllers.sn.client.SNUser
import com.restfb.exception._
import play.Logger
import controllers.sn.exception.AuthException
import controllers.sn.exception.NetworkException
import com.restfb.types.Location
import controllers.sn.exception.LogicException

private[sn] class SocialNetworkClientFacebook extends SocialNetworkClient {
  private val facebookClient = (x: String) => new FacebookClientRepeater(new DefaultFacebookClient(x))

  // TODO: remove this implicit since all requests should be repeated 3 times.
  // TODO: make client private.
  implicit def repeaterToClient(r: FacebookClientRepeater): FacebookClient = r.client

  /// Facebook exception handler.
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
    return SNUserFacebook(
      client.fetchObject("me", classOf[com.restfb.types.User]),
      this,
      token)

  }

  /// Get all social networks friends.
  def fetchFriendsByToken(token: String): List[SNUser] = handleExceptions {
    import collection.JavaConversions._
    facebookClient(token).fetchConnection("me/friends", classOf[com.restfb.types.User]).getData().toList.map(SNUserFacebook(_, this, ""))
  }

  /// Fetches location of user from FB.
  private[facebook] def fetchLocationFromFB(token: String): FQLLocation = {
    val query = "SELECT current_location FROM user WHERE uid=me()"

    val locations = facebookClient(token).executeFqlQuery(query, classOf[FQLLocation])

    if (locations.size < 0 || locations.size > 1) {
      throw new LogicException()
    }

    locations.get(0)
  }

}

private[sn] object SocialNetworkClientFacebook {
  val Name = "FB"

  def apply(): SocialNetworkClientFacebook = new SocialNetworkClientFacebook()
}

