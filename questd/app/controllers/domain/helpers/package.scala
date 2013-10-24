package controllers.domain

import models.store._
import play.Logger

package object helpers {
  
  /**
   * Wrapper for handling unknown exceptions.
   */
  private [domain] def handleUnknownEx[T](f: => ApiResult[T]): ApiResult[T] = try {
    f
  } catch {
    case ex: Throwable => {
      Logger.error("Exceptionally unexpected exception", ex)
      InternalErrorApiResult(None)
    }
  }

  /**
   * Wrapper for handling db exceptions.
   */
  private [domain] def handleDbException[T](f: => ApiResult[T]): ApiResult[T] = handleUnknownEx {
    try {
      f
    } catch {
      case ex: StoreException => {
        Logger.error("DB error during login", ex)
        InternalErrorApiResult(None)
      }
    }
  }
}