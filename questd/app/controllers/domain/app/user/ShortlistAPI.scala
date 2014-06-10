package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import logic._
import play.Logger
import controllers.domain.app.protocol.ProfileModificationResult._

case class GetShortlistRequest(
  user: User)
case class GetShortlistResult(
  allowed: ProfileModificationResult,
  userIds: List[String])

case class CostToShortlistRequest(
  user: User)
case class CostToShortlistResult(
  allowed: ProfileModificationResult,
  cost: Option[Assets] = None)

case class AddToShortlistRequest(
  user: User,
  userIdToAdd: String)
case class AddToShortlistResult(
  allowed: ProfileModificationResult,
  assets: Option[Assets] = None)

case class RemoveFromShortlistRequest(
  user: User,
  userIdToAdd: String)
case class RemoveFromShortlistResult(
  allowed: ProfileModificationResult)

private[domain] trait ShortlistAPI { this: DBAccessor with DomainAPIComponent#DomainAPI =>

  /**
   * Get ids of users from our shortlist.
   */
  def getShortlist(request: GetShortlistRequest): ApiResult[GetShortlistResult] = handleDbException {
    OkApiResult(Some(GetShortlistResult(
      allowed = OK,
      userIds = request.user.shortlist)))
  }

  /**
   * How much it'll take to shortlist person.
   */
  def costToShortlist(request: CostToShortlistRequest): ApiResult[CostToShortlistResult] = handleDbException {
    OkApiResult(Some(CostToShortlistResult(
      allowed = OK,
      cost = Some(request.user.costToShortlist))))
  }

  /**
   * Adds a user to shortlist
   */
  def addToShortlist(request: AddToShortlistRequest): ApiResult[AddToShortlistResult] = handleDbException {

    val maxShortlistSize = 1000

    if (request.user.shortlist.length >= maxShortlistSize) {
      OkApiResult(Some(AddToShortlistResult(LimitExceeded)))
    } else {
      request.user.canShortlist match {
        case OK => {
          {

            makeTask(MakeTaskRequest(request.user, TaskType.AddToShortList))

          } map { r =>

            val cost = request.user.costToShortlist
            adjustAssets(AdjustAssetsRequest(user = r.user, cost = Some(cost)))

          } map { r =>

            db.user.addToShortlist(r.user.id, request.userIdToAdd)
            OkApiResult(Some(AddToShortlistResult(OK, Some(r.user.profile.assets))))
            
          }
        }
        case a => OkApiResult(Some(AddToShortlistResult(a)))
      }
    }

  }

  /**
   * Adds a user to shortlist
   */
  def removeFromShortlist(request: RemoveFromShortlistRequest): ApiResult[RemoveFromShortlistResult] = handleDbException {

    request.user.canShortlist match {
      case OK => {
        db.user.removeFromShortlist(request.user.id, request.userIdToAdd)
        OkApiResult(Some(RemoveFromShortlistResult(OK)))
      }
      case a => OkApiResult(Some(RemoveFromShortlistResult(a)))
    }

  }

}

