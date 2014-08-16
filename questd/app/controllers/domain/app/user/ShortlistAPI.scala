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
  token: String)
case class GetSuggestsForShortlistResult(
  allowed: ProfileModificationResult,
  userIds: Option[List[String]])

private[domain] trait ShortlistAPI { this: DBAccessor with DomainAPIComponent#DomainAPI with SNAccessor =>

  /**
   * Get ids of users from our shortlist.
   */
  def getShortlist(request: GetShortlistRequest): ApiResult[GetShortlistResult] = handleDbException {

    request.user.canShortlist match {
      case OK => {
        OkApiResult(GetShortlistResult(
          allowed = OK,
          userIds = Some(request.user.shortlist)))
      }
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
          {

            makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.AddToShortList)))

          } ifOk { r =>

            val cost = request.user.costToShortlist
            adjustAssets(AdjustAssetsRequest(user = r.user, cost = Some(cost)))

          } ifOk { r =>

            db.user.addToShortlist(r.user.id, request.userIdToAdd)
            OkApiResult(AddToShortlistResult(OK, Some(r.user.profile.assets)))

          }
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
      case OK => {
        db.user.removeFromShortlist(request.user.id, request.userIdToAdd)
        OkApiResult(RemoveFromShortlistResult(OK))
      }
      case a => OkApiResult(RemoveFromShortlistResult(a))
    }

  }

  /**
   * Returns list of users we would like to follow (theoretically).
   */
  def getSuggestsForShortlist(request: GetSuggestsForShortlistRequest): ApiResult[GetSuggestsForShortlistResult] = handleDbException {
    request.user.canShortlist match {
      case OK => {

        // TODO: make abstract SNComponent.
        
        // TODO: catch all FB exceptions here (like in auth).
//        import collection.JavaConversions._
//        import controllers.domain.libs.facebook.FacebookComponent
//        import controllers.domain.libs.facebook.UserFB

//        val fbFriends = fb.fetchConnection(request.token, "me/friends", classOf[UserFB])
//        val friends = (for (i <- fbFriends.getData().toList) yield {
          
          // TODO: optimize it in batch call.
          // TODO: test batch call
          
//          Logger.error("TTTT " + i.getId() + " " + i.getName())
//          db.user.readByFBid(i.getId())
//        }).filter(_ != None).map(_.get.id).filter(!request.user.friends.contains(_)).filter(!request.user.shortlist.contains(_))
        

        // TODO: test each filter here.

//        OkApiResult(GetSuggestsForShortlistResult(OK, Some(friends)))
        // TODO: remove me.
        OkApiResult(GetSuggestsForShortlistResult(OK, None))
      }
      case a => OkApiResult(GetSuggestsForShortlistResult(a, None))
    }
  }

}

