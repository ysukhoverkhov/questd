package models.domain.base

trait ID {
  val id: String
}

object ID {
  def generateUUID(): String = {
    java.util.UUID.randomUUID().toString()
  }
}
