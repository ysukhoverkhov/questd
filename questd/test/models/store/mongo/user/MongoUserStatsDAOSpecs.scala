package models.store.mongo.user

import models.domain.common.ContentVote
import models.domain.user.User
import models.domain.user.stats.SolutionsInBattle
import models.store.mongo.BaseDAOSpecs
import play.api.test.WithApplication
import testhelpers.domainstubs._

/**
 * MongoUserStatsDAO specs
 */
trait MongoUserStatsDAOSpecs { this: BaseDAOSpecs =>
  "Mongo User DAO" should {
    "recordQuestVote works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val u = createUserStub()
      val q = createQuestStub()

      db.user.create(u)
      db.user.recordQuestVote(u.id, q.id, ContentVote.Cheating)

      val ou1 = db.user.readById(u.id)
      ou1 must beSome.which((u: User) => u.stats.votedQuests.get(q.id).contains(ContentVote.Cheating))
    }

    "recordSolutionVote works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val u = createUserStub()
      val s = createSolutionStub()

      db.user.create(u)
      db.user.recordSolutionVote(u.id, s.id, ContentVote.Cheating)

      val ou1 = db.user.readById(u.id)
      ou1 must beSome.which((u: User) => u.stats.votedSolutions.get(s.id).contains(ContentVote.Cheating))
    }

    "recordBattleVote works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val u = createUserStub()
      val s = createSolutionStub()
      val b = createBattleStub()

      db.user.create(u)
      db.user.recordBattleVote(u.id, b.id, s.id)

      val ou1 = db.user.readById(u.id)
      ou1 must beSome.which((u: User) => u.stats.votedBattles.get(b.id).contains(s.id))
    }

    "recordBattleParticipation works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val u = createUserStub()
      val s = createSolutionStub()
      val b = createBattleStub()

      db.user.create(u)
      db.user.recordBattleParticipation(u.id, b.id, SolutionsInBattle(List(s.id)))

      val ou1 = db.user.readById(u.id)
      ou1 must beSome
      ou1.get.stats.participatedBattles.get(b.id).contains(SolutionsInBattle(List(s.id))) must beTrue
    }

    "recordQuestSolving do its work" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val questId = "qiq"
      val solutionId = "siq"
      val user = createUserStub(questBookmark = Some(questId))
      db.user.create(user)

      db.user.recordQuestSolving(user.id, questId, solutionId, removeBookmark = true)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User]
      ou1.get.stats.solvedQuests must beEqualTo(Map(questId -> solutionId))
      ou1.get.profile.questSolutionContext.bookmarkedQuest must beNone
    }

    "Setting notification about registration flag works correctly" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()

      db.user.create(user)

      user.stats.friendsNotifiedAboutRegistration must beEqualTo(false)
      val ou1 = db.user.setFriendsNotifiedAboutRegistrationFlag(user.id, flag = true)
      ou1.get.stats.friendsNotifiedAboutRegistration must beEqualTo(true)
    }
  }
}
