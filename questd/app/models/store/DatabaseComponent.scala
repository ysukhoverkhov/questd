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
    val solution: SolutionDAO
    val battle: BattleDAO
    val culture: CultureDAO
    val tutorial: TutorialDAO
    val tutorialTask: TutorialTaskDAO
    val comment: CommentDAO
    val conversation: ConversationDAO
    val chat: ChatMessageDAO

    val crawlerContext: CrawlerContextDAO
  }

}

