package controllers.web.rest.component

import java.util.UUID

import controllers.domain.OkApiResult
import controllers.web.helpers._
import play.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.reflect.io.Path

private object UploadWSImplTypes {
  /**
   * Response on upload request.
   */
  case class WSUploadResult(
    code: UploadCode.Value,
    contentId: Option[String])

  /**
   *  Upload errors.
   */
  object UploadCode extends Enumeration {
    val OK = Value
    val FileNotFoundInRequest = Value
    val RequestIsNotMultiPart = Value
  }

  case class WSGetContentURLByIdRequest (
    contentId: String)

  case class WSGetContentURLByIdResult(
    code: ContentURlRequestCode.Value,
    url: Option[String])
  object ContentURlRequestCode extends Enumeration {
    val OK = Value
    val ContentNotFount = Value
  }
}

trait UploadWSImpl extends BaseController with SecurityWSImpl { this: WSComponent#WS =>

  import controllers.web.rest.component.UploadWSImplTypes._

  def upload = Authenticated.async { implicit request =>
    Future {
      request.request.body.asMultipartFormData match {
        case Some(data) =>
          data.file("content").map { content =>
//            val contentType = content.contentType

            val filename = content.filename
            val baseDir = Path(config(ConfigParams.ContentUploadDir))
            val addition = (Path(s"${request.user.id}") / UUID.randomUUID().toString).addExtension(Path(filename).extension)
            val file = (baseDir / addition).jfile

            file.getParentFile.mkdirs()
            content.ref.moveTo(file)
            file.setReadable(true, false)

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

