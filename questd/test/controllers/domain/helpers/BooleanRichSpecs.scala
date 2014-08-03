package controllers.domain.helpers

import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.matcher._


class BooleanRichSpecs extends Specification {

  "BooleanRich" should {
    
    "Perform xoring correctly" in {
      
      false ^^ false must beEqualTo(false)
      false ^^ true must beEqualTo(true)
      true ^^ false must beEqualTo(true)
      true ^^ true must beEqualTo(false)
    }

  }

}

