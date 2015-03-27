package com.vita.scala

/**
 * Scala language extensions made by Vita
 */
package object Extensions {

  import scala.language.implicitConversions
  implicit def enrichBoolean(v: Boolean): RichBoolean = new RichBoolean(v)
  implicit def enrichEnumeration[T <: Enumeration](v: T): RichEnumeration[T] = new RichEnumeration[T](v)

}
