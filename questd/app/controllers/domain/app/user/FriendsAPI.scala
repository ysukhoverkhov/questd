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

//case class CostToShortlistRequest(
//  user: User)
//case class CostToShortlistResult(
//  allowed: ProfileModificationResult,
//  cost: Option[Assets] = None)
//
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

//  /**
//   * How much it'll take to shortlist person.
//   */
//  def costToShortlist(request: CostToShortlistRequest): ApiResult[CostToShortlistResult] = handleDbException {
//    OkApiResult(Some(CostToShortlistResult(
//      allowed = OK,
//      cost = Some(request.user.costToShortlist))))
//  }
//
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

