package models.store.mongo

import models.store._
import dao._

trait MongoDatabaseComponent extends DatabaseComponent {

  class MongoDatabase extends Database {

    val theme = new MongoThemeDAO
    val config = new MongoConfigDAO
    val tutorial = new MongoTutorialDAO
    val tutorialTask = new MongoTutorialTaskDAO

    val user = new MongoUserDAO
    val quest = new MongoQuestDAO
    val solution = new MongoSolutionDAO
    val battle = new MongoBattleDAO
    val culture = new MongoCultureDAO
    val comment = new MongoCommentDAO
    val conversation = new MongoConversationDAO
    val chat = new MongoChatMessageDAO

    val crawlerContext = new MongoCrawlerContextDAO
  }
}

