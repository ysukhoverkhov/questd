package controllers.web.rest.component

import controllers.web.rest.component.helpers._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

trait UploadWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  def upload = Authenticated.async { implicit request =>
    Future {
      request.request.body.asMultipartFormData match {
        case Some(data) =>
          data.file("content").map { content =>
//            val filename = content.filename
//            val contentType = content.contentType

            val baseDir = "" // TODO: load from config.
            val baseURL = "" // TODO: load from config.
            // TODO: invent directory here.
//            content.ref.moveTo(new File("d:/picture.jpg"))
            // TODO: tell client url back.
            Ok("File uploaded")
          }.getOrElse {
            BadRequest("File is missing")
          }
        case          None =>
          BadRequest("MultipartFormData is missing")
      }
    }
  }
}

