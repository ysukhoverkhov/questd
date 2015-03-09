package helpers

package object rich {

  import scala.language.implicitConversions
  implicit def enrichBoolean(v: Boolean): RichBoolean = new RichBoolean(v)
  implicit def enrichEnumeration[T <: Enumeration](v: T): RichEnumeration[T] = new RichEnumeration[T](v)
}
