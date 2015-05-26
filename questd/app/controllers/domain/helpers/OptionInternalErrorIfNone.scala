package controllers.domain.helpers

import controllers.domain.InternalErrorApiResult
import controllers.domain.ApiResult


class OptionInternalErrorIfNone [T] (val option: Option[T]) {

  def ifSome[ART, RT >: ApiResult[ART]](f: T => RT): RT = {
    option match {
      case Some(v) => f(v)
      case None => InternalErrorApiResult("None is there Some expected")
    }
  }

}

