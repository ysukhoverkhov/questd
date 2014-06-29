package components.random

import scala.util.Random

private[random] class RandomImplementation {
  private val _rand = new Random(System.currentTimeMillis())
  
  def nextInt = _rand.nextInt
  def nextInt(i: Int) = _rand.nextInt(i)
  
  def nextDouble = _rand.nextDouble
}

