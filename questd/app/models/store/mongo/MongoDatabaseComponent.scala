package models.store.mongo

import models.store._
import models.store.dao._
import dao._

trait MongoDatabaseComponent extends DatabaseComponent {

  class MongoDatabase extends Database {

    val theme = new MongoThemeDAO
    val config = new MongoConfigDAO
    val tutorialTask = new MongoTutorialTaskDAO

    val user = new MongoUserDAO
    val quest = new MongoQuestDAO
    val solution = new MongoQuestSolutionDAO
    val culture = new MongoCultureDAO
  }

}

