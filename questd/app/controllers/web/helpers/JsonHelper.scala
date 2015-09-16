package controllers.web.helpers

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.{write => swrite}

import scala.reflect.ClassTag


object JsonHelper {

  private class EnumGeneralNameSerializer[E <: Enumeration: ClassTag]()
    extends Serializer[E#Value] {
    import JsonDSL._

    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), E#Value] = {
      case x if false => throw new MappingException("Enums deserializaion is not implemented")
    }

    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case i: E#Value => i.toString
    }
  }

  private implicit val formats = org.json4s.native.Serialization.formats(NoTypeHints) +
    new EnumGeneralNameSerializer


  /**
   * Writes object to json.
   *
   * @param a Object to write.
   * @tparam A Type of object to write.
   * @return Json in form of string.
   */
  def write[A <: AnyRef](a: A): String = swrite(a)

  /**
   * Writes object to json.
   *
   * @param a Object to write.
   * @param serializers Additional serializers to use.
   * @tparam A Type of object to write.
   * @return Json in form of string.
   */
  def write[A <: AnyRef](a: A, serializers: Traversable[org.json4s.Serializer[_]]): String = swrite(a)(formats ++ serializers)

  /**
   * Reads function from json string to object.
   *
   * @param json String with json to parse.
   * @tparam A Type of object to return.
   * @return Parsed object.
   */
  def read[A <: AnyRef : Manifest](json: String): A = parse(json).extract[A]

  /**
   * Reads function from json string to object.
   *
   * @param json String with json to parse.
   * @param serializers Additional serializers to use.
   * @tparam A Type of object to return.
   * @return Parsed object.
   */
  def read[A <: AnyRef : Manifest](json: String, serializers: Traversable[org.json4s.Serializer[_]]): A = parse(json).extract[A](formats ++ serializers, manifest)
}
