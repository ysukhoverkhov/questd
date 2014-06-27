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

case class GetTutorialStateRequest(user: User, platformId: String)
case class GetTutorialStateResult(state: Option[String])

case class SetTutorialStateRequest(user: User, platformId: String, state: String)
case class SetTutorialStateResult(allowed: ProfileModificationResult)

private[domain] trait TutorialAPI { this: DBAccessor =>

  /**
   * Get state of tutorial for a specified platform.
   */
  def getTutorialState(request: GetTutorialStateRequest): ApiResult[GetTutorialStateResult] = handleDbException {
    import request._

    OkApiResult(Some(GetTutorialStateResult(user.tutorial.clientTutorialState.get(platformId))))
  }

  /**
   * Set state of tutorial for a specified platform.
   */
  def setTutorialState(request: SetTutorialStateRequest): ApiResult[SetTutorialStateResult] = handleDbException {
    import request._

    if (user.tutorial.clientTutorialState.size > logic.constants.NumberOfStoredTutorialPlatforms ||
      state.length > logic.constants.MaxLengthOfTutorialPlatformState) {
      OkApiResult(Some(SetTutorialStateResult(LimitExceeded)))
    } else {

      db.user.setTutorialState(user.id, platformId, state)
      OkApiResult(Some(SetTutorialStateResult(OK)))
    }

  }

}



// TODO: remove option from OkApiResult
