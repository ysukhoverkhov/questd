package controllers.domain.apiresult

sealed abstract class ApiResult {
  def body: Option[Map[String, String]]
}

case class OkApiResult (body: Option[Map[String, String]]) extends ApiResult
case class NotAuthorisedApiResult (body: Option[Map[String, String]]) extends ApiResult
case class InternalErrorApiResult (body: Option[Map[String, String]]) extends ApiResult

