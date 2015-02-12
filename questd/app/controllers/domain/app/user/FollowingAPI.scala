package controllers.domain.app.user

import models.domain._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.sn.client.{User => SNUser}

case class GetFollowingRequest(
  user: User)
case class GetFollowingResult(
  allowed: ProfileModificationResult,
  userIds: Option[List[String]])

case class GetFollowersRequest(
  user: User)
case class GetFollowersResult(
  allowed: ProfileModificationResult,
  userIds: Option[List[String]])

case class CostToFollowingRequest(
  user: User)
case class CostToFollowingResult(
  allowed: ProfileModificationResult,
  cost: Option[Assets] = None)

case class AddToFollowingRequest(
  user: User,
  userIdToAdd: String)
case class AddToFollowingResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)

case class RemoveFromFollowingRequest(
  user: User,
  userIdToAdd: String)
case class RemoveFromFollowingResult(
  allowed: ProfileModificationResult)

case class GetSuggestsForFollowingRequest(
  user: User,
  // keys - SN names, values - tokens for them.
  tokens: Map[String, String])

case class GetSuggestsForFollowingResult(
  allowed: ProfileModificationResult,
  userIds: Option[List[String]] = None)

private[domain] trait FollowingAPI { this: DBAccessor with DomainAPIComponent#DomainAPI with SNAccessor =>

  /**
   * Get ids of users from our following.
   */
  def getFollowing(request: GetFollowingRequest): ApiResult[GetFollowingResult] = handleDbException {

    request.user.canFollow match {
      case OK =>
        OkApiResult(GetFollowingResult(
          allowed = OK,
          userIds = Some(request.user.following)))
      case a => OkApiResult(GetFollowingResult(a, None))
    }
  }

  /**
   * Get ids of users who follows us.
   */
  def getFollowers(request: GetFollowersRequest): ApiResult[GetFollowersResult] = handleDbException {
        OkApiResult(GetFollowersResult(
          allowed = OK,
          userIds = Some(request.user.followers)))
  }

  /**
   * How much it'll take to following person.
   */
  def costToFollowing(request: CostToFollowingRequest): ApiResult[CostToFollowingResult] = handleDbException {
    OkApiResult(CostToFollowingResult(
      allowed = OK,
      cost = Some(request.user.costToFollowing)))
  }

  /**
   * Adds a user to following
   */
  def addToFollowing(request: AddToFollowingRequest): ApiResult[AddToFollowingResult] = handleDbException {

    val maxFollowingSize = 1000

    if (request.user.following.length >= maxFollowingSize) {
      OkApiResult(AddToFollowingResult(LimitExceeded))
    } else {
      request.user.canFollow match {
        case OK => {

          makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.AddToFollowing)))

        } ifOk { r =>

          val cost = request.user.costToFollowing
          adjustAssets(AdjustAssetsRequest(user = r.user, cost = Some(cost)))

        } ifOk { r =>

          db.user.addToFollowing(r.user.id, request.userIdToAdd)
          OkApiResult(AddToFollowingResult(OK, Some(r.user.profile)))

        }
        case a => OkApiResult(AddToFollowingResult(a))
      }
    }

  }

  /**
   * Removes user from following (following).
   */
  def removeFromFollowing(request: RemoveFromFollowingRequest): ApiResult[RemoveFromFollowingResult] = handleDbException {

    request.user.canFollow match {
      case OK =>
        db.user.removeFromFollowing(request.user.id, request.userIdToAdd)
        OkApiResult(RemoveFromFollowingResult(OK))
      case a => OkApiResult(RemoveFromFollowingResult(a))
    }

  }

  /**
   * Returns list of users we would like to follow (theoretically).
   */
  def getSuggestsForFollowing(request: GetSuggestsForFollowingRequest): ApiResult[GetSuggestsForFollowingResult] = handleDbException {
    request.user.canFollow match {
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
        }).filter(_ != None).map(_.get).map(_.id).filter(!request.user.friends.contains(_)).filter(!request.user.following.contains(_))

        OkApiResult(GetSuggestsForFollowingResult(OK, Some(friends)))
      case a => OkApiResult(GetSuggestsForFollowingResult(a))
    }
  }
}

