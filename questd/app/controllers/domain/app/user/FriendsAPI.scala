package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import play.Logger
import controllers.domain.app.protocol.ProfileModificationResult._

case class GetFriendsRequest(
  user: User)
case class GetFriendsResult(
  allowed: ProfileModificationResult,
  userIds: List[Friendship])

case class CostToRequestFriendshipRequest(
  user: User,
  friendId: String)
case class CostToRequestFriendshipResult(
  allowed: ProfileModificationResult,
  cost: Option[Assets] = None)

//case class AddToShortlistRequest(
//  user: User,
//  userIdToAdd: String)
//case class AddToShortlistResult(
//  allowed: ProfileModificationResult,
//  assets: Option[Assets] = None)
//
//case class RemoveFromShortlistRequest(
//  user: User,
//  userIdToAdd: String)
//case class RemoveFromShortlistResult(
//  allowed: ProfileModificationResult)

private[domain] trait FriendsAPI { this: DBAccessor with DomainAPIComponent#DomainAPI =>

  /**
   * Get ids of users from our shortlist.
   */
  def getFriends(request: GetFriendsRequest): ApiResult[GetFriendsResult] = handleDbException {
    OkApiResult(Some(GetFriendsResult(
      allowed = OK,
      userIds = request.user.friends)))
  }

  /**
   * How much it'll take invite persone to become a friend.
   */
  def costToRequestFriendship(request: CostToRequestFriendshipRequest): ApiResult[CostToRequestFriendshipResult] = handleDbException {

    db.user.readByID(request.friendId) match {
      case Some(u) => {
        OkApiResult(Some(CostToRequestFriendshipResult(
          allowed = OK,
          cost = Some(request.user.costToAddFriend(u)))))
      }
      
      case None => {
        OkApiResult(Some(CostToRequestFriendshipResult(
          allowed = OutOfContent)))
      }
    }
  }

  //  /**
  //   * Adds a user to shortlist
  //   */
  //  def addToShortlist(request: AddToShortlistRequest): ApiResult[AddToShortlistResult] = handleDbException {
  //
  //    val maxShortlistSize = 1000
  //    
  //    request.user.canShortlist match {
  //      case OK => {
  //
  //        val cost = request.user.costToShortlist
  //        adjustAssets(AdjustAssetsRequest(user = request.user, cost = Some(cost))) map { r =>
  //
  //          if (request.user.shortlist.length >= maxShortlistSize) {
  //            OkApiResult(Some(AddToShortlistResult(LimitExceeded)))
  //          } else {
  //            db.user.addToShortlist(r.user.id, request.userIdToAdd)
  //            OkApiResult(Some(AddToShortlistResult(OK, Some(r.user.profile.assets))))
  //          }
  //        }
  //      }
  //      case a => OkApiResult(Some(AddToShortlistResult(a)))
  //    }
  //
  //  }
  //
  //  /**
  //   * Adds a user to shortlist
  //   */
  //  def removeFromShortlist(request: RemoveFromShortlistRequest): ApiResult[RemoveFromShortlistResult] = handleDbException {
  //
  //    request.user.canShortlist match {
  //      case OK => {
  //        db.user.removeFromShortlist(request.user.id, request.userIdToAdd)
  //        OkApiResult(Some(RemoveFromShortlistResult(OK)))
  //      }
  //      case a => OkApiResult(Some(RemoveFromShortlistResult(a)))
  //    }
  //
  //  }

}

