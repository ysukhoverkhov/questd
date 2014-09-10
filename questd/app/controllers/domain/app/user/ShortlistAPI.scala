package controllers.domain.app.user

import models.domain._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.sn.client.SNUser

case class GetShortlistRequest(
  user: User)
case class GetShortlistResult(
  allowed: ProfileModificationResult,
  userIds: Option[List[String]])

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

case class GetSuggestsForShortlistRequest(
  user: User,
  // keys - SN names, values - tokens for them.
  tokens: Map[String, String])

case class GetSuggestsForShortlistResult(
  allowed: ProfileModificationResult,
  userIds: List[String] = List())

private[domain] trait ShortlistAPI { this: DBAccessor with DomainAPIComponent#DomainAPI with SNAccessor =>

  /**
   * Get ids of users from our shortlist.
   */
  def getShortlist(request: GetShortlistRequest): ApiResult[GetShortlistResult] = handleDbException {

    request.user.canShortlist match {
      case OK =>
        OkApiResult(GetShortlistResult(
          allowed = OK,
          userIds = Some(request.user.shortlist)))
      case a => OkApiResult(GetShortlistResult(a, None))
    }

  }

  /**
   * How much it'll take to shortlist person.
   */
  def costToShortlist(request: CostToShortlistRequest): ApiResult[CostToShortlistResult] = handleDbException {
    OkApiResult(CostToShortlistResult(
      allowed = OK,
      cost = Some(request.user.costToShortlist)))
  }

  /**
   * Adds a user to shortlist
   */
  def addToShortlist(request: AddToShortlistRequest): ApiResult[AddToShortlistResult] = handleDbException {

    val maxShortlistSize = 1000

    if (request.user.shortlist.length >= maxShortlistSize) {
      OkApiResult(AddToShortlistResult(LimitExceeded))
    } else {
      request.user.canShortlist match {
        case OK => {

          makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.AddToShortList)))

        } ifOk { r =>

          val cost = request.user.costToShortlist
          adjustAssets(AdjustAssetsRequest(user = r.user, cost = Some(cost)))

        } ifOk { r =>

          db.user.addToShortlist(r.user.id, request.userIdToAdd)
          OkApiResult(AddToShortlistResult(OK, Some(r.user.profile.assets)))

        }
        case a => OkApiResult(AddToShortlistResult(a))
      }
    }

  }

  /**
   * Removes user from shortlist (following).
   */
  def removeFromShortlist(request: RemoveFromShortlistRequest): ApiResult[RemoveFromShortlistResult] = handleDbException {

    request.user.canShortlist match {
      case OK =>
        db.user.removeFromShortlist(request.user.id, request.userIdToAdd)
        OkApiResult(RemoveFromShortlistResult(OK))
      case a => OkApiResult(RemoveFromShortlistResult(a))
    }

  }

  /**
   * Returns list of users we would like to follow (theoretically).
   */
  def getSuggestsForShortlist(request: GetSuggestsForShortlistRequest): ApiResult[GetSuggestsForShortlistResult] = handleDbException {
    request.user.canShortlist match {
      case OK =>

        val snFriends = request.tokens.foldLeft(List[SNUser]()) { (r, v) =>
          try {
            r ::: sn.clientForName(v._1).fetchFriendsByToken(v._2)
          } catch {
            case _ : Throwable =>
              r
          }
        }

        val friends = (for (i <- snFriends) yield {

          // optimize it in batch call.
          // test batch call

          db.user.readBySNid(i.snName, i.snId)
        }).filter(_ != None).map(_.get).map(_.id).filter(!request.user.friends.contains(_)).filter(!request.user.shortlist.contains(_))

        OkApiResult(GetSuggestsForShortlistResult(OK, friends))
      case a => OkApiResult(GetSuggestsForShortlistResult(a))
    }
  }
}

