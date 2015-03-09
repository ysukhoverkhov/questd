package controllers.web.rest.component


import java.util.UUID

import controllers.domain.OkApiResult
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
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
            val addition = Path(s"${request.user.id}") / UUID.randomUUID().toString
            val file = (baseDir / addition).jfile

            file.getParentFile.mkdirs()
            content.ref.moveTo(file)

            Ok(
              Json.write(WSUploadResult(UploadCode.OK, Some(addition.toString())))).as(JSON)

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

  def getContentURLById = wrapJsonApiCallReturnBody[WSGetContentURLByIdResult] { (js, r) =>
    val v = Json.read[WSGetContentURLByIdRequest](js)

    val baseURL = config(ConfigParams.UploadedContentBaseURL)

    OkApiResult(WSGetContentURLByIdResult(ContentURlRequestCode.OK, Some(baseURL + v.contentId)))
  }

}

