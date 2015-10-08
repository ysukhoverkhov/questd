package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.services.socialnetworks.client.{User => SNUser}
import models.domain.common.{ContentReference, ContentType}
import models.domain.user._
import models.domain.user.auth.{AuthInfo, LoginMethod}
import models.domain.user.profile.{Bio, Profile, PublicProfile}
import play.Logger

case class LoginRequest(
  snName: String,
  snuser: SNUser,
  referrerId: Option[String] = None,
  invitedWithContentId: Option[String] = None)
case class LoginResult(sessionId: String, userId: String)

private[domain] trait AuthAPI {
  this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Login with FB. Or create new one if it doesn't exists.
   */
  def login(request: LoginRequest): ApiResult[LoginResult] = handleDbException {

    def loginUser(user: User) = {
      val uuid = java.util.UUID.randomUUID().toString

      db.user.updateSessionId(user.id, uuid) ifSome { u =>
        {
          updateCrossPromotion(UpdateCrossPromotionRequest(u, request.snuser))
        } map {
          updateUserCulture(UpdateUserCultureRequest(u))
        } map {
          notifySNFriendsAboutLogin(NotifySNFriendsAboutLoginRequest(u, request.snuser)) // TODO: est it's called.
        } map {
          OkApiResult(LoginResult(uuid, u.id))
        }
      }
    }

    def createUserAndLogin() = {
      Logger.debug("No user with FB id found, creating new one " + request.snuser.snId)

      val newUser = User(
        auth = AuthInfo(
          loginMethods = List(LoginMethod(
            methodName = request.snName,
            userId = request.snuser.snId))),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              name = request.snuser.firstName,
              gender = request.snuser.gender,
              timezone = request.snuser.timezone,
              country = request.snuser.country,
              city = request.snuser.city,
              avatar = Some(
                ContentReference(contentType = ContentType.Photo, storage = "fb_avatar", reference = request.snuser.snId))))))

      {
        createUser(CreateUserRequest(newUser))
      } map { r =>
        request.referrerId.fold() { referrerId =>

          api.createFriendship(
            CreateFriendshipRequest(
              user = r.user,
              friendId = referrerId,
              isReferredBy = true,
              referredWithContentId = request.invitedWithContentId
            ))
        }

        Logger.debug(s"New user created with FB: ${r.user.id} / ${r.user.profile.publicProfile.bio.name}")
        loginUser(r.user)
      }
    }

    Logger.debug(s"Searching for user in database for login with FBid ${request.snuser.snId}")

    db.user.readBySNid(request.snName, request.snuser.snId) match {
      case None =>
        createUserAndLogin()

      case Some(user) =>
        Logger.debug(s"Existing user login with FB: ${user.id} / ${user.profile.publicProfile.bio.name}")
        loginUser(user)
    }
  }

}

