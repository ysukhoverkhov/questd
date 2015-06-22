package logic.user

import logic.BaseLogicSpecs
import com.mongodb.casbah.commons.MongoDBList
import com.mongodb.BasicDBList
import com.mongodb.casbah.commons.Implicits._
import models.domain.user.User

class CommonUtilSpecs extends BaseLogicSpecs {

  "CommonUtil from user logic" should {

    "Find string in list of lists of strings" in {
      val test = List(
        List("a", "b", "c"),
        List("b", "c", "e"),
        List("f", "g", "h"))

      val u = User()

      u.listOfListsContainsString(test, (x: String) => x, "a") must beTrue
      u.listOfListsContainsString(test, (x: String) => x, "b") must beTrue
      u.listOfListsContainsString(test, (x: String) => x, "1") must beFalse
      u.listOfListsContainsString(test, (x: String) => x, "h") must beTrue
    }


    "Find string in list of lists of DBObjects" in {
      val r1: BasicDBList = MongoDBList("a", "b", "c")
      val r2: BasicDBList = MongoDBList("b", "c", "e")
      val r3: BasicDBList = MongoDBList("f", "g", "h")

      val test = List(
        r1,
        r2,
        r3)

      val u = User()

      u.listOfListsContainsString(test.asInstanceOf[List[List[String]]], (x: String) => x, "a") must beTrue
      u.listOfListsContainsString(test.asInstanceOf[List[List[String]]], (x: String) => x, "b") must beTrue
      u.listOfListsContainsString(test.asInstanceOf[List[List[String]]], (x: String) => x, "1") must beFalse
      u.listOfListsContainsString(test.asInstanceOf[List[List[String]]], (x: String) => x, "h") must beTrue
    }

  }

}
