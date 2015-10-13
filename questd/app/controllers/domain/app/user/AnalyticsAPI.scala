package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain.user._
import models.domain.user.profile.Profile


case class SetUserSourceRequest(
  user: User,
  userSource: String)
case class SetUserSourceResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)


private[domain] trait AnalyticsAPI { this: DBAccessor with DomainAPIComponent#DomainAPI =>

  /**
   * Sets user source if it's not set.
   */
  def setUserSource(request: SetUserSourceRequest): ApiResult[SetUserSourceResult] = handleDbException {
    import request._

    if (user.profile.analytics.source.isDefined) {
      OkApiResult(SetUserSourceResult(InvalidState))
    } else {
      // make task to optimize existence calls.
      db.user.setUserSource(user.id, userSource) ifSome { u =>
        OkApiResult(SetUserSourceResult(OK, Some(u.profile)))
      }
    }
  }
}

