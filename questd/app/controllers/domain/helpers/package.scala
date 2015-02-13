package controllers.domain

package object helpers {

  import scala.language.implicitConversions
  implicit def optionToOIERIN[T](o: Option[T]): OptionInternalErrorIfNone[T] = new OptionInternalErrorIfNone(o)

  private [domain] def handleUnknownEx[P, T >: ApiResult[P]] = exceptionwrappers.handleUnknownEx[P, T] _
  private [domain] def handleDbException[P, T >: ApiResult[P]] = exceptionwrappers.handleDbException[P, T] _
}

