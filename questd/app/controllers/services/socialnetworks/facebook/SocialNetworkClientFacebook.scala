package controllers.services.socialnetworks.facebook

import com.restfb._
import com.restfb.exception._
import com.restfb.json.JsonObject
import controllers.services.socialnetworks.client._
import controllers.services.socialnetworks.exception.{AuthException, NetworkException}
import controllers.services.socialnetworks.facebook.types.UserIdWithApp
import play.Logger

import scala.language.implicitConversions


private[socialnetworks] class SocialNetworkClientFacebook extends SocialNetworkClient {

  private val facebookClient = (x: String) => new FacebookClientRepeater(new DefaultFacebookClient(x, Version.VERSION_2_0))

  /// Facebook exception handler.
  private def handleExceptions[T](f: => T): T = try {
    f
  } catch {
    case ex: FacebookOAuthException =>
      Logger.debug("Facebook call failed")
      throw new AuthException
    case ex: FacebookNetworkException =>
      Logger.debug("Unable to connect to Facebook")
      throw new NetworkException
  }

  /**
   * @inheritdoc
   */
  def isValidUserToken(token: String): Boolean = handleExceptions {
    val client = facebookClient("971303889546582|9vp9yrm76V-8-TlIYlxjFTKQWKg")

    val rv = client.fetchObject("debug_token", classOf[JsonObject], Parameter.`with`("input_token", token))

    Logger.error(s"$rv")
    if (rv.has("error")) {
      false
    } else {
      val data = rv.getJsonObject("data")
      (data.getString("app_id") == "971303889546582") && data.getBoolean("is_valid") // TODO: get app id and secret from config.
    }
  }

  /// Get user of social network.
  def fetchUserByToken(token: String): User = handleExceptions {

    val client = facebookClient(token)
    UserFacebook(
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

  /**
   * @inheritdoc
   */
  def fetchIdsInOtherApps(token: String): List[UserIdInApplication] = {
    import collection.JavaConversions._

    facebookClient(token).fetchConnection(
      "me/ids_for_business", classOf[UserIdWithApp]).getData.toList.map(UserIdInApplicationFacebook(_))
  }
}


private[socialnetworks] object SocialNetworkClientFacebook {
  val Name = "FB"

  def apply(): SocialNetworkClientFacebook = new SocialNetworkClientFacebook()
}

