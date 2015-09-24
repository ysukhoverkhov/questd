package models.domain.base

abstract class ID {
  val id: String
}

object ID {
  def generate: String = java.util.UUID.randomUUID().toString
}
