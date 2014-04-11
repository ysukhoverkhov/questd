package components.random

trait RandomComponent {
  val rand: Random
  
  class Random extends RandomImplementation 
}

