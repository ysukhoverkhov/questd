package components.random

import scala.util.Random

private[random] class RandomImplementation {
  private val _rand = new Random(System.currentTimeMillis())

  def nextInt() = _rand.nextInt()
  def nextInt(i: Int) = _rand.nextInt(i)

  def nextDouble() = _rand.nextDouble()

  def nextGaussian(mean: Double, dev: Double) = _rand.nextGaussian() * dev + mean

  def nextGaussian(mean: Double = 0, dev: Double = 1, min: Double = Double.MinValue): Double = {
    nextGaussian(mean, dev) match {
      case a if a < min => min
      case a => a
    }
  }
}

