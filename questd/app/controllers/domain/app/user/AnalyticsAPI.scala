package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.CommonCode
import controllers.domain.helpers._
import models.domain.user._
import models.domain.user.profile.Profile

object SetUserSourceCode extends Enumeration with CommonCode {
  val SourceAlreadySet = Value
}
case class SetUserSourceRequest(
  user: User,
  userSource: Map[String, String])
case class SetUserSourceResult(
  allowed: SetUserSourceCode.Value,
  profile: Option[Profile] = None)


private[domain] trait AnalyticsAPI { this: DBAccessor with DomainAPIComponent#DomainAPI =>

  /**
   * Sets user source if it's not set.
   */
  def setUserSource(request: SetUserSourceRequest): ApiResult[SetUserSourceResult] = handleDbException {
    import SetUserSourceCode._
    import request._

    if (user.profile.analytics.source.nonEmpty) {
      OkApiResult(SetUserSourceResult(SourceAlreadySet))
    } else {
      // make task to optimize existence calls.
      db.user.setUserSource(user.id, userSource) ifSome { u =>
        OkApiResult(SetUserSourceResult(OK, Some(u.profile)))
      }
    }
  }
}

