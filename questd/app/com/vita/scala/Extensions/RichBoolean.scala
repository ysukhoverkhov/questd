package com.vita.scala.extensions

class RichBoolean (val v: Boolean) {

  def ^^(o: Boolean) = {
    (v || o) && !(v && o)
  }

}
