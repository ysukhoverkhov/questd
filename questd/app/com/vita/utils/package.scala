package com.vita

package object utils {
  import _root_.scala.language.implicitConversions

  implicit def integerToCallRepeater(i: Int): CallRepeater = new CallRepeater(i)
}
