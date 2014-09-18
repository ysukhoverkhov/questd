package controllers.sn.facebook

import controllers.sn.client.{Invitation, SocialNetworkClient, User}
import scala.language.implicitConversions
import com.restfb._
import com.restfb.exception._
import play.Logger
import controllers.sn.exception.AuthException
import controllers.sn.exception.NetworkException
import controllers.sn.exception.LogicException

private[sn] class SocialNetworkClientFacebook extends SocialNetworkClient {

  private val facebookClient = (x: String) => new FacebookClientRepeater(new DefaultFacebookClient(x, Version.VERSION_2_0))

  /// Facebook exception handler.
  private def handleExceptions[T](f: => T): T = try {
    f
  } catch {
    case ex: FacebookOAuthException =>
      Logger.debug("Facebook auth failed")
      throw new AuthException
    case ex: FacebookNetworkException =>
      Logger.debug("Unable to connect to facebook")
      throw new NetworkException
  }

  /// Get user of social network.
  def fetchUserByToken(token: String): User = handleExceptions {

    val client = facebookClient(token)
    return UserFacebook(
      client.fetchObject("me", classOf[com.restfb.types.User]),
      this,
      token)

  }

  /// Get all social networks friends.
  def fetchFriendsByToken(token: String): List[User] = handleExceptions {
    import collection.JavaConversions._
    facebookClient(token).fetchConnection(
      "me/friends", classOf[com.restfb.types.User]).getData.toList.map(UserFacebook(_, this, ""))
  }

  /**
   * @inheritdoc
   */
  def fetchInvitations(token: String): List[Invitation] = {
    import collection.JavaConversions._

    facebookClient(token).fetchConnection(
      "me/apprequests", classOf[com.restfb.types.AppRequest]).getData.toList.map(InvitationFacebook(_, this, token))
  }

  /**
   * @inheritdoc
   */
  def deleteInvitation(token: String, invitation: Invitation): Unit = {
    invitation match {
      case fbi: InvitationFacebook =>
        facebookClient(token).deleteObject(fbi.snId)
      case _ =>
        Logger.error("A try to delete not facebook invitation with facebook client, doing nothing")
    }
  }

  /// Fetches location of user from FB.
  private[facebook] def fetchLocationFromFB(token: String): FQLLocation = handleExceptions {
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

