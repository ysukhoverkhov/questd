package components

import models.store._

trait DBAccessor {
  val db: DatabaseComponent#Database
}

