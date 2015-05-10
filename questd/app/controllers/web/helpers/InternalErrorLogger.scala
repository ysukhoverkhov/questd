package controllers.web.helpers

import play.api._
import play.api.mvc._

trait InternalErrorLogger extends Controller {

  // This is like this to forbid InternalServerError usage.
  private[this] var null_isr: Status = _
  override val InternalServerError: Status = null_isr

  /**
   * Log current stack trace and report server error.
   */
  def ServerError = {
    try {
      throw new Exception("Internal server error")
    } catch {
      case ex: Throwable => Logger.error("Internal server error", ex)
    }

    new Status(INTERNAL_SERVER_ERROR)
  }

}
