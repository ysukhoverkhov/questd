package controllers.domain

sealed abstract class ApiResult[+T] {
  def body: Option[T]
}

final case class OkApiResult[T] (body: Option[T]) extends ApiResult[T]
final case class NotFoundApiResult[T] (body: Option[T]) extends ApiResult[T]
final case class NotAuthorisedApiResult[T] (body: Option[T]) extends ApiResult[T]
final case class InternalErrorApiResult[T] (body: Option[T]) extends ApiResult[T]

