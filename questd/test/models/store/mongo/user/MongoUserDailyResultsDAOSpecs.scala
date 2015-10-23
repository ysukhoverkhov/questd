package models.store.mongo.user

import java.util.Date

import models.domain.common.Assets
import models.domain.user.User
import models.domain.user.dailyresults.BattleResult
import models.store.mongo.BaseDAOSpecs
import play.api.test.WithApplication
import testhelpers.domainstubs._

/**
 * MongoUserDailyResultsDAO specs
 */
trait MongoUserDailyResultsDAOSpecs { this: BaseDAOSpecs =>
  "Mongo User DAO" should {
    "storeQuestSolvingInDailyResult do its work" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val quest = createQuestStub()
      val user = createUserStub(
        privateDailyResults = List(createDailyResultStub(
          questsIncome = List(createQuestIncomeStub(questId = quest.id)))))
      val reward = Assets(1, 2, 3)

      db.user.create(user)
      db.user.storeQuestSolvingInDailyResult(user.id, quest.id, reward)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User]
      val u = ou1.get
      u.privateDailyResults.head.questsIncome.head.timesSolved must beEqualTo(1)
      u.privateDailyResults.head.questsIncome.head.solutionsIncome must beEqualTo(reward)
    }

    "addQuestIncomeToDailyResult do its work" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val quest = createQuestStub()
      val user = createUserStub(
        privateDailyResults = List(createDailyResultStub(
          questsIncome = List(createQuestIncomeStub(questId = quest.id)))))
      val reward = Assets(1, 2, 3)

      db.user.create(user)
      db.user.addQuestIncomeToDailyResult(user.id, createQuestIncomeStub())

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User]
      val u = ou1.get
      u.privateDailyResults.head.questsIncome.length must beEqualTo(2)
    }

    "removeQuestIncomeFromDailyResult do its work" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val quest = createQuestStub()
      val user = createUserStub(
        privateDailyResults = List(createDailyResultStub(
          questsIncome = List(createQuestIncomeStub(questId = quest.id)))))
      val reward = Assets(1, 2, 3)

      db.user.create(user)
      db.user.removeQuestIncomeFromDailyResult(user.id, quest.id)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User]
      val u = ou1.get
      u.privateDailyResults.head.questsIncome.length must beEqualTo(0)
    }

    "storeBattleInDailyResult works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val br = BattleResult("1", Assets(1, 2, 3), isVictory = true)

      db.user.create(user)
      db.user.storeBattleInDailyResult(user.id, br)

      val ou = db.user.readById(user.id)

      ou must beSome
      ou.get.privateDailyResults.head.decidedBattles.head must beEqualTo(br)
    }

    "movePrivateDailyResultsToPublic works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val results = (1 to 5).map(i => createDailyResultStub(startOfPeriod = new Date(i))).toList

      val user = createUserStub(privateDailyResults = results)

      db.user.create(user)
      val ou = db.user.movePrivateDailyResultsToPublic(user.id, results.tail)

      ou must beSome
      ou.get.privateDailyResults must beEqualTo(List(results.head))
      ou.get.profile.dailyResults must beEqualTo(results.tail)
    }
  }
}
