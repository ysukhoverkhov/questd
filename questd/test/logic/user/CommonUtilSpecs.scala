package logic.user

import org.specs2.mutable.Specification
import org.specs2.matcher.BeEqualTo
import models.domain.User

class CommonUtilSpecs extends BaseUserLogicSpecs {

  "CommonUtil from user logic" should {

    "Find string in list of lists of strings" in {
      val test = List(
        List("a", "b", "c"),
        List("b", "c", "e"),
        List("f", "g", "h"))

      val u = User()
      
      u.listOfListsContainsString(test, ((x: String) => x), "a") must beTrue
      u.listOfListsContainsString(test, ((x: String) => x), "b") must beTrue
      u.listOfListsContainsString(test, ((x: String) => x), "1") must beFalse
      u.listOfListsContainsString(test, ((x: String) => x), "h") must beTrue
    }

    "Find string in list of lists of DBObjects" in {
      val test = List(
        List("a", "b", "c"),
        List("b", "c", "e"),
        List("f", "g", "h"))

      val u = User()
      
      u.listOfListsContainsString(test, ((x: String) => x), "a") must beTrue
      u.listOfListsContainsString(test, ((x: String) => x), "b") must beTrue
      u.listOfListsContainsString(test, ((x: String) => x), "1") must beFalse
      u.listOfListsContainsString(test, ((x: String) => x), "h") must beTrue
    }
  
  }

}
