package controllers.web.rest

import play.api.libs.json._
import controllers.domain.user._
import play.Logger

// TODO CRITICAL clean here everything up.
// TODO CRITICAL - do not serialize ot string and the parse to Jsvalue. put string to response (be sure it's type is "application/json").
// TODO remove deserialization from implementation what will allow us to remove argument to serializer which makes things a lot more easy and allows us to write uniform seriializer
// for AnyRef (what we will do)

package object protocol {

   
  /**
   * Payload in case of 401 error.
   */
  case class WSUnauthorisedResult(code: UnauthorisedReason.Value)

  /**
   *  Reasons of Unauthorised results.
   */ 
  object UnauthorisedReason extends Enumeration {
    
    /**
     *  FB tells us it doesn't know the token.
     */ 
    val InvalidFBToken = Value(1)
    
    /**
     *  Supplied session is not valid on our server.
     */ 
    val SessionNotFound = Value(2)
  }

  implicit val unathorisedWrites = new Writes[WSUnauthorisedResult] {
    def writes(c: WSUnauthorisedResult): JsValue = Json.obj("code" -> c.code.id)
  }

  
  /**
   * Login Request
   * Single entry. Key - "token", value - value.
   */
  type WSLoginFBRequest = Map[String, String]

  /**
   * Login Result
   * Single entry. Key - "token", value - value.
   */
  case class WSLoginFBResult(sessionid: String)

  implicit val loginFBResultWrites = new Writes[WSLoginFBResult] {
    def writes(c: WSLoginFBResult): JsValue = Json.obj("sessionid" -> c.sessionid)
  }
  
  /**
   * Get Quest theme cost result
   */
  type WSGetQuestThemeCostResult = GetQuestThemeCostResult
  
  implicit val getQuestThemeCostResultWrites = new Writes[WSGetQuestThemeCostResult] {
    
    
    def writes(c: WSGetQuestThemeCostResult): JsValue = {
	    import org.json4s.native.Serialization.{read, write => swrite}
	    import org.json4s._
	    
	    import controllers.domain.user.protocol.ProfileModificationResult
import scala.reflect.ClassTag

	    
	    class EnumSerializer[E <: Enumeration: ClassTag](enum: E)
  extends Serializer[E#Value] {
  import JsonDSL._

  val EnumerationClass = classOf[E#Value]

  private[this] def isValid(json: JValue) = json match {
    case JInt(value) => value <= enum.maxId
    case _ => false
  }

  def deserialize(implicit format: Formats):
    PartialFunction[(TypeInfo, JValue), E#Value] = {
      case (TypeInfo(EnumerationClass, _), json) if isValid(json) => json match {
        case JInt(value) => enum(value.toInt)
        case value => throw new MappingException("Can't convert " +
          value + " to "+ EnumerationClass)
      }
    }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case i: E#Value => i.id
  }
}

class EnumNameSerializer[E <: Enumeration: ClassTag](enum: E)
  extends Serializer[E#Value] {
  import JsonDSL._

  val EnumerationClass = classOf[E#Value]

  def deserialize(implicit format: Formats):
    PartialFunction[(TypeInfo, JValue), E#Value] = {
      case (t @ TypeInfo(EnumerationClass, _), json) if (isValid(json)) => {
        json match {
         case JString(value) => enum.withName(value)
          case value => throw new MappingException("Can't convert " +
            value + " to "+ EnumerationClass)
        }
      }
    }

  private[this] def isValid(json: JValue) = json match {
    case JString(value) if (enum.values.exists(_.toString == value)) => true
    case _ => false
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case i: E#Value => i.toString
  }
}
	    
	    
	    
        implicit val formats = org.json4s.native.Serialization.formats(NoTypeHints) + new EnumNameSerializer(ProfileModificationResult)
	      
	    val s = swrite(c)
	    Logger.error(s)
	    Json.parse(s)
    }
  }
  
}
