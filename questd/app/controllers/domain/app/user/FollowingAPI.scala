package controllers.domain.app.user

import components._
import controllers.domain.app.protocol.CommonCode
import controllers.domain.helpers._
import controllers.domain.{DomainAPIComponent, _}
import controllers.services.socialnetworks.client.{Permission, User => SNUser}
import models.domain.common.Assets
import models.domain.user.User
import models.domain.user.message.MessageFriendRegistered
import models.domain.user.profile.{Profile, TaskType}
import play.Logger

object GetFollowingCode extends Enumeration with CommonCode {
}
case class GetFollowingRequest(
  user: User)
case class GetFollowingResult(
  allowed: GetFollowingCode.Value,
  userIds: Option[List[String]])


object GetFollowersCode extends Enumeration with CommonCode {
}
case class GetFollowersRequest(
  user: User)
case class GetFollowersResult(
  allowed: GetFollowersCode.Value,
  userIds: Option[List[String]])


object CostToFollowingCode extends Enumeration with CommonCode {
}
case class CostToFollowingRequest(
  user: User)
case class CostToFollowingResult(
  allowed: CostToFollowingCode.Value,
  cost: Option[Assets] = None)


object AddToFollowingCode extends Enumeration with CommonCode {
  val FollowingCountLimitExceeded = Value
  val UserNotFound = Value
  val CantFollowMySelf = Value
}
case class AddToFollowingRequest(
  user: User,
  userIdToAdd: String)
case class AddToFollowingResult(
  allowed: AddToFollowingCode.Value,
  profile: Option[Profile] = None)


object RemoveFromFollowingCode extends Enumeration with CommonCode {
}
case class RemoveFromFollowingRequest(
  user: User,
  userIdToRemove: String)
case class RemoveFromFollowingResult(
  allowed: RemoveFromFollowingCode.Value,
  profile: Option[Profile] = None)


object GetSuggestsForFollowingCode extends Enumeration with CommonCode {
}
case class GetSuggestsForFollowingRequest(
  user: User,
  // keys - SN names, values - tokens for them.
  tokens: Map[String, String])
case class GetSuggestsForFollowingResult(
  allowed: GetSuggestsForFollowingCode.Value,
  userIds: List[String] = List.empty)


object GetSNFriendsInGameCode extends Enumeration with CommonCode {
}
case class GetSNFriendsInGameRequest(
  user: User,
  // keys - SN names, values - tokens for them.
  tokens: Map[String, String])
case class GetSNFriendsInGameResult(
  allowed: GetSNFriendsInGameCode.Value,
  userIds: List[String] = List.empty)


case class NotifySNFriendsAboutLoginRequest(
  user: User,
  snUser: SNUser)
case class NotifySNFriendsAboutLoginResult(user: User)



private[domain] trait FollowingAPI { this: DBAccessor with DomainAPIComponent#DomainAPI with SNAccessor =>

  /**
   * Get ids of users from our following.
   */
  def getFollowing(request: GetFollowingRequest): ApiResult[GetFollowingResult] = handleDbException {
    import GetFollowingCode._

    OkApiResult(GetFollowingResult(
      allowed = OK,
      userIds = Some(request.user.following)))
  }

  /**
   * Get ids of users who follows us.
   */
  def getFollowers(request: GetFollowersRequest): ApiResult[GetFollowersResult] = handleDbException {
    import GetFollowersCode._

    OkApiResult(GetFollowersResult(
      allowed = OK,
      userIds = Some(request.user.followers.filterNot(request.user.banned.toSet))))
  }

  /**
   * How much it'll take to following person.
   */
  def costToFollowing(request: CostToFollowingRequest): ApiResult[CostToFollowingResult] = handleDbException {
    import CostToFollowingCode._

    OkApiResult(CostToFollowingResult(
      allowed = OK,
      cost = Some(request.user.costToFollowing)))
  }

  /**
   * Adds a user to following
   */
  def addToFollowing(request: AddToFollowingRequest): ApiResult[AddToFollowingResult] = handleDbException {
    import AddToFollowingCode._

    val maxFollowingSize = api.config(api.DefaultConfigParams.FollowingUsersMaxLength).toInt

    if (request.user.following.length >= maxFollowingSize) {
      OkApiResult(AddToFollowingResult(FollowingCountLimitExceeded))
    } else {
      db.user.readById(request.userIdToAdd).fold[ApiResult[AddToFollowingResult]] {
        OkApiResult(AddToFollowingResult(UserNotFound))
      } { userToFollow =>

        request.user.canFollowUser(request.userIdToAdd) match {
          case OK => {

            makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.AddToFollowing)))

          } map { r =>

            val cost = request.user.costToFollowing
            adjustAssets(AdjustAssetsRequest(user = r.user, change = -cost))

          } map { r =>

            db.user.addToFollowing(r.user.id, request.userIdToAdd)
            OkApiResult(AddToFollowingResult(OK, Some(r.user.profile)))

          }

          case reason if reason == NotEnoughAssets || reason == NotEnoughRights =>
            OkApiResult(AddToFollowingResult(
              allowed = reason,
              profile = Some(request.user.profile)))

          case result =>
            OkApiResult(AddToFollowingResult(result))
        }
      }
    }

  }

  /**
   * Removes user from following (following).
   */
  def removeFromFollowing(request: RemoveFromFollowingRequest): ApiResult[RemoveFromFollowingResult] = handleDbException {
    import RemoveFromFollowingCode._

    db.user.removeFromFollowing(request.user.id, request.userIdToRemove) ifSome { r =>
      OkApiResult(RemoveFromFollowingResult(OK, Some(r.profile)))
    }
  }

  /**
   * Returns list of users we would like to follow (theoretically).
   */
  def getSuggestsForFollowing(request: GetSuggestsForFollowingRequest): ApiResult[GetSuggestsForFollowingResult] = handleDbException {
    import GetSuggestsForFollowingCode._

    OkApiResult(GetSuggestsForFollowingResult(OK))
  }

  /**
   * Returns list of our friends who is already in the game.
   */
  def getSNFriendsInGame(request: GetSNFriendsInGameRequest): ApiResult[GetSNFriendsInGameResult] = handleDbException {
    import GetSNFriendsInGameCode._

    val snFriends = request.tokens.foldLeft(List[SNUser]()) { (r, v) =>
      try {
        r ::: sn.clientForName(v._1).fetchFriendsByToken(v._2)
      } catch {
        case _ : Throwable =>
          r
      }
    }

    val friends = (for (i <- snFriends) yield {

      // OPTIMIZE: optimize it in batch call.
      // OPTIMIZE: test batch call

      db.user.readBySNid(i.snName, i.snId)
    }).filter(_.isDefined)
      .map(_.get)
      .map(_.id)
      .filter(!request.user.friends.map(_.friendId).contains(_))
      .filter(!request.user.following.contains(_))
      .filter(!request.user.banned.contains(_))

    OkApiResult(GetSNFriendsInGameResult(OK, friends))
  }


  /**
   * Decides should we notify friends about or login and notifies them.
   * @return
   */
  def notifySNFriendsAboutLogin(request: NotifySNFriendsAboutLoginRequest): ApiResult[NotifySNFriendsAboutLoginResult] = handleDbException {
    import request._

    if (request.user.stats.friendsNotifiedAboutRegistration) {
      OkApiResult(NotifySNFriendsAboutLoginResult(user))
    } else {
      if (snUser.permissions.contains(Permission.Friends)) {
        snUser.friends.foreach { snFriend =>
          db.user.readBySNid(snFriend.snName, snFriend.snId).fold {
            Logger.error(s"unable to find user for notification of friends about registration $snFriend")
          } { snFriend: User =>
            sendMessage(SendMessageRequest(snFriend, MessageFriendRegistered(user.id)))
          }
        }

        db.user.setFriendsNotifiedAboutRegistrationFlag(id = user.id, flag = true) ifSome { u =>
          OkApiResult(NotifySNFriendsAboutLoginResult(u))
        }
      } else {
        OkApiResult(NotifySNFriendsAboutLoginResult(user))
      }
    }
  }
}

