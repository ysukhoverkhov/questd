package models.domain.base

abstract class ID {
  val id: String
}

object ID {
  def generateUUID(): String = java.util.UUID.randomUUID().toString // TODO: it should be renamed to "generate"
}
