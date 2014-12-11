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
            import java.io.File
            val filename = content.filename
            val contentType = content.contentType
            // TODO: invent directory here.
            content.ref.moveTo(new File("d:/picture.jpg"))
            // TODO: tell client url back.
            Ok("File uploaded")
          }.getOrElse {
            BadRequest("File is missing")
          }
        case          None =>
          BadRequest("MultipartFormData is missing")
      }

//
//      try {
//        apiCall(request) match {
//          case OkApiResult(r) => {
//            body(r)
//          }
//
//          case NotAuthorisedApiResult() => Unauthorized(
//            Json.write(WSUnauthorisedResult(UnauthorisedReason.SessionNotFound))).as(JSON)
//
//          case a =>
//            Logger.error(s"Unexpected result in api call - $a")
//            ServerError
//        }
//      } catch {
//        case ex @ (_: MappingException | _: org.json4s.ParserUtil$ParseException) => {
//          BadRequest(ex.getMessage())
//        }
//        case ex: Throwable => {
//          Logger.error("Api calling exception", ex)
//          ServerError
//        }
//      }
    }
  }


}

