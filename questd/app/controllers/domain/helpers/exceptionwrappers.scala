package controllers.domain.helpers

import models.store._
import play.Logger
import controllers.domain._

object exceptionwrappers {

  /**
   * Wrapper for handling unknown exceptions.
   */
  private [helpers] def handleUnknownEx[P, T >: ApiResult[P]](f: => T): T = try {
    f
  } catch {
    case ex: Throwable =>
      Logger.error("Exceptionally unexpected exception", ex)
      InternalErrorApiResult()
  }

  /**
   * Wrapper for handling db exceptions.
   */
  private [helpers] def handleDbException[P, T >: ApiResult[P]](f: => T): T = handleUnknownEx {
    try {
      f
    } catch {
      case ex: DatabaseException =>
        Logger.error("DB error during login", ex)
        InternalErrorApiResult()
    }
  }

}
