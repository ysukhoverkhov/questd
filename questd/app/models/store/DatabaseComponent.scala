package models.store

import models.store.dao._

/**
 * Main component for the database.
 */
trait DatabaseComponent {

  protected val db: Database

  trait Database {
    val user: UserDAO
    val theme: ThemeDAO 
    val config: ConfigDAO
    val quest: QuestDAO
    val solution: QuestSolutionDAO
  }

}

