package controllers.web.helpers

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.{write => swrite}

import scala.reflect.ClassTag


object JsonHelper {


  class EnumGeneralNameSerializer[E <: Enumeration: ClassTag]()
    extends Serializer[E#Value] {
    import JsonDSL._

    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), E#Value] = {
      case x if false => throw new MappingException("Enums deserializaion is not implemented")
    }

    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case i: E#Value => i.toString
    }
  }

  implicit val formats = org.json4s.native.Serialization.formats(NoTypeHints) +
    new EnumGeneralNameSerializer

  def write[A <: AnyRef](a: A): String = swrite(a)

  def write[A <: AnyRef](a: A, serializers: Traversable[org.json4s.Serializer[_]]): String = swrite(a)(formats ++ serializers)

  def read[A <: AnyRef : Manifest](json: String): A = parse(json).extract[A]

  def read[A <: AnyRef : Manifest](json: String, serializers: Traversable[org.json4s.Serializer[_]]): A = parse(json).extract[A](formats ++ serializers, manifest)
}
