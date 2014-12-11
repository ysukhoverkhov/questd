package controllers.web.rest.component


import java.util.UUID

import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol.{UploadCode, WSUploadResult}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.reflect.io.Path

trait UploadWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  def upload = Authenticated.async { implicit request =>
    Future {
      request.request.body.asMultipartFormData match {
        case Some(data) =>
          data.file("content").map { content =>
//            val filename = content.filename
//            val contentType = content.contentType

            val baseDir = Path(config(ConfigParams.ContentUploadDir))
            val baseURL = config(ConfigParams.UploadedContentBaseURL)
            val addition = Path(s"${request.user.id}") / UUID.randomUUID().toString
            val file = (baseDir / addition).jfile

            file.getParentFile.mkdirs()
            content.ref.moveTo(file)

            Ok(
              Json.write(WSUploadResult(UploadCode.OK, Some(baseURL + addition)))).as(JSON)

          }.getOrElse {
            BadRequest(
              Json.write(WSUploadResult(UploadCode.FileNotFoundInRequest, None))).as(JSON)
          }
        case None =>
          BadRequest(
            Json.write(WSUploadResult(UploadCode.RequestIsNotMultiPart, None))).as(JSON)
      }
    }
  }
}

