package models.store

import models.store.dao._

/**
 * Main component for the database.
 */
trait DatabaseComponent {

  val db: Database

  trait Database
    extends UserDAO
    with ThemeDAO

}

