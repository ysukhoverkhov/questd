package com.vita.utils

import scala.annotation.tailrec

private [utils] class CallRepeater (count: Int) {
  def times(f: => Unit): Unit = loop(f, count)

  @tailrec
  private def loop (f: => Unit, n: Int): Unit = if (n > 0) { f; loop(f, n - 1) }
}

