package controllers.domain

sealed abstract class ApiResult[+T] {
  def body: Option[T]

  def ifOk[T2](f: T => ApiResult[T2]): ApiResult[T2] = {
    this match {
      case OkApiResult(r) => f(r)
      case NotFoundApiResult() => NotFoundApiResult()
      case NotAuthorisedApiResult() => NotAuthorisedApiResult()
      case InternalErrorApiResult(a) => InternalErrorApiResult(a)

    }
  }

  def ifOk[T2](f: => ApiResult[T2]): ApiResult[T2] = {
    this match {
      case OkApiResult(_) => f
      case NotFoundApiResult() => NotFoundApiResult()
      case NotAuthorisedApiResult() => NotAuthorisedApiResult()
      case InternalErrorApiResult(a) => InternalErrorApiResult(a)
    }
  }
}

final case class OkApiResult[T](value: T) extends ApiResult[T] {
  val body = Some(value)
}
final case class NotFoundApiResult() extends ApiResult[Nothing] { val body = None }
final case class NotAuthorisedApiResult() extends ApiResult[Nothing] { val body = None }
final case class InternalErrorApiResult(
  error: Exception = new Exception("Unknown InternalErrorApiResult")
  ) extends ApiResult[Nothing] {
    val body = None

    def this(msg: String) = this(new Exception(msg))

    override def toString = s"InternalErrorApiResult\n$error"
  }
object InternalErrorApiResult {
  def apply(msg: String): InternalErrorApiResult = new InternalErrorApiResult(msg)
}

