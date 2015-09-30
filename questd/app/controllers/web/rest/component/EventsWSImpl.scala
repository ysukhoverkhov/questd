package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._
import controllers.web.rest.component.EventsWSImplTypes._
import models.domain.common.ClientPlatform
import com.vita.scala.extensions._

private object EventsWSImplTypes {

  case class WSRemoveMessageRequest(
    /// Id of a message to remove.
    id: String)
  type WSRemoveMessageResult = RemoveMessageResult

  case class WSAddDeviceTokenRequest (

    /// id of a platform to add device to.
    platform: String,

    /// Token of a device to be added.
    token: String
  )
  type WSAddDeviceTokenResult = AddDeviceTokenResult

  case class WSRemoveDeviceTokenRequest (
    /// Token of a device to be removed.
    token: String
  )
  type WSRemoveDeviceTokenResult = RemoveDeviceTokenResult
}

trait EventsWSImpl extends BaseController with SecurityWSImpl { this: WSComponent#WS =>
  import EventsWSImplTypes.{WSRemoveMessageRequest, WSRemoveMessageResult}

  /**
   * @return
   */
  def removeMessage() = wrapJsonApiCallReturnBody[WSRemoveMessageResult] { (js, r) =>
    val v = Json.read[WSRemoveMessageRequest](js.toString)

    api.removeMessage(RemoveMessageRequest(r.user, v.id))
  }

  /**
   * @return
   */
  //noinspection MutatorLikeMethodIsParameterless
  def addDeviceToken = wrapJsonApiCallReturnBody[WSAddDeviceTokenResult] { (js, r) =>
    val v = Json.read[WSAddDeviceTokenRequest](js.toString)

    api.addDeviceToken(AddDeviceTokenRequest(r.user, ClientPlatform.withNameEx(v.platform), v.token))
  }

  /**
   * @return
   */
  //noinspection MutatorLikeMethodIsParameterless
  def removeDeviceToken = wrapJsonApiCallReturnBody[RemoveDeviceTokenResult] { (js, r) =>
    val v = Json.read[WSRemoveDeviceTokenRequest](js.toString)

    api.removeDeviceToken(RemoveDeviceTokenRequest(r.user, v.token))
  }

}

