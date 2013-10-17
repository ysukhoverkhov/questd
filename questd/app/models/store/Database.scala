package models.store

import DAOs._

abstract class Database {
  def user: UserDAO
}

