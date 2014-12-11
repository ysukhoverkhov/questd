package controllers.web.rest

import controllers.web.rest.component.helpers.AccessToWSInstance
import play.api.mvc._

object UploadWS extends Controller with AccessToWSInstance {

  def upload = ws.upload
}

