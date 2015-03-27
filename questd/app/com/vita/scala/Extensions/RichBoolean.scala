package com.vita.scala.Extensions

class RichBoolean (val v: Boolean) {

  def ^^(o: Boolean) = {
    (v || o) && !(v && o)
  }

}
