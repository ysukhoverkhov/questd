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

case class GetShortlistRequest(
  user: User,
  pageNumber: Int,
  pageSize: Int)
case class GetShortlistResult(
  allowed: ProfileModificationResult,
  users: List[Bio],
  pageSize: Int,
  hasMore: Boolean)

private[domain] trait ShortlistAPI { this: DBAccessor =>

  /**
   * Reset all purchases (quests and themes) overnight.
   */
//  def resetCounters(request: ResetCountersRequest): ApiResult[ResetCountersResult] = handleDbException {
//    import request._
//
//    db.user.resetCounters(user.id, user.getResetPurchasesTimeout)
//
//    OkApiResult(Some(ResetCountersResult()))
//  }

}

