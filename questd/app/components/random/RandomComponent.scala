package components.random

trait RandomComponent {
  protected val rand: Random

  class Random extends RandomImplementation
}

