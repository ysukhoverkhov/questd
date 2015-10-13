package models.store.mongo.user

import java.util.Date

import models.domain.quest.QuestRating
import models.domain.user.User
import models.domain.user.profile.{QuestCreationContext, Profile}
import models.store.mongo.BaseDAOSpecs
import models.view.QuestView
import play.api.test.WithApplication
import testhelpers.domainstubs._

/**
 * MongoUserContextsDAO specs
 */
trait MongoUserContextsDAOSpecs { this: BaseDAOSpecs =>

  "Mongo User DAO" should {
    "updateQuestCreationCoolDown should reset cool down" in new WithApplication(appWithTestDatabase) {
      val userId = "resetQuestProposal"
      val date = new Date(1000)
      val dateNew = new Date(2000)

      db.user.clear()

      db.user.create(
        User(
          id = userId,
          profile = Profile(
            questCreationContext = QuestCreationContext(
              questCreationCoolDown = date))))

      val ou = db.user.updateQuestCreationCoolDown(userId, dateNew)

      ou must beSome.which((u: User) => u.id.toString == userId)
      ou must beSome.which((u: User) => u.profile.questCreationContext.questCreationCoolDown == dateNew)
    }

    "setQuestBookmark do its work" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val questId = "qiq"
      val qi = QuestView(
        questId,
        createQuestStub(id = questId).info,
        QuestRating(),
        None,
        None)
      val user = createUserStub(questBookmark = None)
      db.user.create(user)

      db.user.setQuestBookmark(user.id, qi)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User]
      ou1.get.profile.questSolutionContext.bookmarkedQuest must beEqualTo(Some(qi))
    }
  }
}
