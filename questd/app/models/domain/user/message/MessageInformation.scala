package models.domain.user.message

import scala.language.implicitConversions


/**
 * A message about removed friendship connection.
 */
case class MessageInformation (text: String, url: Option[String])


/**
 * Companion object
 */
object MessageInformation {
  implicit def toMessage(a: MessageInformation): Message = {
    Message(
      messageType = MessageType.Information,
      data = a.url.fold[Map[String, String]](Map.empty)(url => Map("url" -> url)) ++ Map("text" -> a.text))
  }
}
