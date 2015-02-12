package controllers.web.rest.component.helpers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import controllers.web.rest.component._
import controllers.domain._
import controllers.web.rest.protocol._
import org.json4s._

private[component] trait CommonFunctions { this: QuestController with SecurityWSImpl =>

  def wrapApiCall[T <: AnyRef](apiCall: AuthenticatedRequest[AnyContent] => ApiResult[T])(body: T => Result) = Authenticated.async { implicit request =>
    Future {

      try {
        apiCall(request) match {
          case OkApiResult(r) =>
            body(r)

          case NotAuthorisedApiResult() =>
            Unauthorized(
              Json.write(WSUnauthorisedResult(UnauthorisedReason.SessionNotFound))).as(JSON)

          case InternalErrorApiResult(ex) =>
            Logger.error("InternalErrorApiResult", ex)
            ServerError

          case a =>
            Logger.error(s"Unexpected result in api call - $a")
            ServerError
        }
      } catch {
        case ex @ (_: MappingException | _: org.json4s.ParserUtil$ParseException | _: java.util.NoSuchElementException) =>
          BadRequest(ex.getMessage)
        case ex: Throwable =>
          Logger.error("Api calling exception", ex)
          ServerError
      }
    }
  }

  def writeBodyInResponse[T <: AnyRef](body: T): Result = {
    Ok(Json.write[T](body)).as(JSON)
  }

  def wrapApiCallReturnBody[T <: AnyRef](apiCall: AuthenticatedRequest[AnyContent] => ApiResult[T]) = wrapApiCall(apiCall)(writeBodyInResponse)

  def wrapJsonApiCallReturnBody[T <: AnyRef](jsonApiCall: (String, AuthenticatedRequest[AnyContent]) => ApiResult[T]) = wrapApiCall { r =>

    r.body.asJson.fold {
      throw new org.json4s.ParserUtil$ParseException("Empty request", null)
    } { js =>
      jsonApiCall(js.toString(), r)
    }

  }(writeBodyInResponse)
}
