package models.store

import DAOs._

/**
 * Main component for the database.
 */
trait DatabaseComponent {
 
  val db: Database
  
  trait Database extends UserDAO
  
}

