package controllers.domain

sealed abstract class ApiResult[+T] {
  def body: Option[T]
}

final case class OkApiResult[T] (body: Option[T]) extends ApiResult[T]
final case class NotFoundApiResult() extends ApiResult[Nothing] { val body = None }
final case class NotAuthorisedApiResult() extends ApiResult[Nothing] { val body = None }
final case class InternalErrorApiResult() extends ApiResult[Nothing] { val body = None }

