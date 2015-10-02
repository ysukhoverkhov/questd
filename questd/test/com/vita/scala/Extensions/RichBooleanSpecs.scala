package com.vita.scala.extensions

import org.specs2.mutable.Specification

class RichBooleanSpecs extends Specification {

  "RichBoolean" should {

    "Perform xoring correctly" in {

      false ^^ false must beEqualTo(false)
      false ^^ true must beEqualTo(true)
      true ^^ false must beEqualTo(true)
      true ^^ true must beEqualTo(false)
    }

  }

}

