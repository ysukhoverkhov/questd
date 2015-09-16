package controllers.web.helpers

import controllers.domain.{InternalErrorApiResult, OkApiResult, ApiResult}
import controllers.web.rest.component.SecurityWSImpl
import play.api._
import play.api.mvc.Security.AuthenticatedRequest
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import org.json4s._

private[web] trait CommonFunctions { this: QuestController with SecurityWSImpl =>

  def wrapApiCallReturnBody[T <: AnyRef](apiCall: AuthenticatedRequest[AnyContent] => ApiResult[T]) = apiCallToResult(apiCall)(writeAnyInResponse)

  def wrapJsonApiCallReturnBody[T <: AnyRef](jsonApiCall: (String, Security.AuthenticatedRequest[AnyContent]) => ApiResult[T]) = apiCallToResult { r =>

    r.body.asJson.fold {
      throw new org.json4s.ParserUtil$ParseException("Empty request", null)
    } { js =>
      jsonApiCall(js.toString(), r)
    }

  }(writeAnyInResponse)

  def wrapReturnAny[T <: AnyRef](value: AuthenticatedRequest[AnyContent] => T) = anyToResult(value)(writeAnyInResponse)

  private def composeAsyncResult(concreteComposer: AuthenticatedRequest[AnyContent] => Result) = Authenticated.async { implicit request =>
    Future {
      concreteComposer(request)
    }
  }

  private def anyToResult[T <: AnyRef](value: AuthenticatedRequest[AnyContent] => T)(resultGenerator: T => Result) = composeAsyncResult { implicit request =>
    resultGenerator(value(request))
  }

  private def apiCallToResult[T <: AnyRef](apiCall: AuthenticatedRequest[AnyContent] => ApiResult[T])(resultGenerator: T => Result) = composeAsyncResult { implicit request =>
    try {
        apiCall(request) match {
          case OkApiResult(r) =>
            resultGenerator(r)

          case InternalErrorApiResult(ex) =>
            Logger.error("InternalErrorApiResult", ex)
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


  // Result generators
  private def writeAnyInResponse[T <: AnyRef](value: T): Result = {
    Ok(Json.write[T](value)).as(JSON)
  }
}
