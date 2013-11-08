package controllers.web.rest.component.helpers

object _Json {
 
  import org.json4s.native.Serialization.{ read, write => swrite }
  import org.json4s._

  import controllers.domain.user.protocol.ProfileModificationResult
  import scala.reflect.ClassTag

  class EnumNameSerializer[E <: Enumeration: ClassTag]()
    extends Serializer[E#Value] {
    import JsonDSL._

    // Can take deserialization from here: https://github.com/json4s/json4s/blob/master/ext/src/main/scala/org/json4s/ext/EnumSerializer.scala
    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), E#Value] = throw new MappingException("Enums deserializaion is not implemented")

    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case i: E#Value => i.toString
    }
  }

  implicit val formats = org.json4s.native.Serialization.formats(NoTypeHints) + new EnumNameSerializer

  
  def write[A <: AnyRef](a: A): String = swrite(a)
  
}