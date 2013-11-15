package models.domain

trait ID {
  val id: String
}

object ID {
  def generateUUID(): String = {
    java.util.UUID.randomUUID().toString()
  }
}
