package models.store

import DAOs._
/*
trait DatabaseComponent {
  val db: Database
  
  trait Database extends UserDAO
  
}

*/

abstract class Database {
  def user: UserDAO
}

