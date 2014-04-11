package components.random

import scala.util.Random

private[random] class RandomImplementation {
  private val rand = new Random(System.currentTimeMillis())
  
  def nextInt = rand.nextInt
  def nextInt(i: Int) = rand.nextInt(i)
  
  def nextDouble = rand.nextDouble
}

