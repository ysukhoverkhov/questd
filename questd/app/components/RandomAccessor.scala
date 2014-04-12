package components

import controllers.domain._
import components.random.RandomComponent

trait RandomAccessor {
  val rand: RandomComponent#Random
}

