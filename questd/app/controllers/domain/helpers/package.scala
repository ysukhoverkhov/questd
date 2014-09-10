package controllers.domain

package object helpers {

  import scala.language.implicitConversions
  implicit def optionToOIERIN[T](o: Option[T]) = new OptionInternalErrorIfNone(o)

  implicit def enrichBoolean(v: Boolean) = new BooleanRich(v)
  
  private [domain] def handleUnknownEx[P, T >: ApiResult[P]] = exceptionwrappers.handleUnknownEx[P, T] _
  private [domain] def handleDbException[P, T >: ApiResult[P]] = exceptionwrappers.handleDbException[P, T] _ 

}
