package controllers.domain

sealed abstract class ApiResult[+T] {
  def body: Option[T]

  def map[T2](f: T => ApiResult[T2]): ApiResult[T2] = {
    this match {
      case OkApiResult(Some(r)) => {
        f(r)
      }

      case OkApiResult(None) => InternalErrorApiResult()
      case NotFoundApiResult() => NotFoundApiResult()
      case NotAuthorisedApiResult() => NotAuthorisedApiResult()
      case InternalErrorApiResult() => InternalErrorApiResult()

    }
  }
  
  def map[T2](f: => ApiResult[T2]): ApiResult[T2] = map(r => f)
  
}

final case class OkApiResult[T](body: Option[T]) extends ApiResult[T]
final case class NotFoundApiResult() extends ApiResult[Nothing] { val body = None }
final case class NotAuthorisedApiResult() extends ApiResult[Nothing] { val body = None }
final case class InternalErrorApiResult() extends ApiResult[Nothing] { val body = None }

