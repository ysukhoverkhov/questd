package helpers.rich

import org.specs2.mutable.Specification

class RichEnumerationSpecs extends Specification {

  "RichEnumeration should" should {

    "Have correct withNameOption" in {

      object TestEnum extends Enumeration {
        val Fighting, Resolved = Value
      }

      TestEnum.withNameOption("missing") must beNone
      TestEnum.withNameOption("Fighting") must beEqualTo(Some(TestEnum.Fighting))
    }

  }

}
