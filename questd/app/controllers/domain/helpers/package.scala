package controllers.domain

package object helpers {

  import scala.language.implicitConversions
  implicit def optionToOIERIN[T](o: Option[T]): OptionInternalErrorIfNone[T] = new OptionInternalErrorIfNone(o)

  private [domain] def handleUnknownEx[P, T >: ApiResult[P]] = exceptionwrappers.handleUnknownEx[P, T] _
  private [domain] def handleDbException[P, T >: ApiResult[P]] = exceptionwrappers.handleDbException[P, T] _

  private [domain] def adjustedPageSize = PagerHelper.adjustedPageSize _
  private [domain] def adjustedPageNumber = PagerHelper.adjustedPageNumber _

  private [domain] def runWhileSome[T](subject: T)(functions: ((T) => Option[T])*): Option[T] = ChainCallsHelper.runWhileSome[T](subject)(functions)
}

