package controllers.web

package object helpers {
  trait BaseController extends InternalErrorLogger

  val Json = helpers.JsonHelper
}
