package components.random

import components.RandomAccessor

trait RandomComponent {
  protected val rand: Random
  
  class Random extends RandomImplementation 
}

