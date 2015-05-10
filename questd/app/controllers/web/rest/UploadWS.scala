package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._

object UploadWS extends Controller with AccessToWSInstance {

  def upload = ws.upload
  def getContentURLById = ws.getContentURLById
}

