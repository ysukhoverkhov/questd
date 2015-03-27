

package models.store.mongo

import java.util.Date

import models.domain._
import models.domain.view.QuestView
import models.store._
import org.specs2.mutable._
import play.api.test._
import testhelpers.domainstubs._

//@RunWith(classOf[JUnitRunner])
class UserDAOSpecs
  extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  "Mongo User DAO" should {
    "Create new User in DB and find it by userId" in new WithApplication(appWithTestDatabase) {
      db.user.clear()
      val userid = "lalala"
      db.user.create(User(userid))
      val u = db.user.readById(userid)
      u must beSome.which((u: User) => u.id.toString == userid)
    }

    "Find user by FB id" in new WithApplication(appWithTestDatabase) {
      val fbid = "idid_fbid"
      val user_id = "session name"
      db.user.create(User(user_id, AuthInfo(snids = Map("FB" -> fbid))))
      val u = db.user.readBySNid("FB", fbid)

      u must beSome
      u must beSome.which((u: User) => u.id == user_id)
    }

    "Find user by session id" in new WithApplication(appWithTestDatabase) {
      val sessid = "idid"
      val testsess = "session name"
      db.user.create(User(testsess, AuthInfo(session = Some(sessid))))
      val u = db.user.readBySessionId(sessid)
      u must beSome.which((u: User) => u.id.toString == testsess) and
        beSome.which((u: User) => u.auth.snids == Map()) and
        beSome.which((u: User) => u.auth.session == Some(sessid))
    }

    "Update user in DB" in new WithApplication(appWithTestDatabase) {
      val sessid = "old session id"
      val id = "id for test of update"

      db.user.create(User(id, AuthInfo(session = Some(sessid))))
      val u1: Option[User] = db.user.readBySessionId(sessid)

      u1 must beSome

      val u1unlifted: User = u1.get

      val newsessid = "very new session id"
      db.user.update(u1unlifted.copy(auth = u1unlifted.auth.copy(session = Some(newsessid))))
      val u2 = db.user.readById(u1unlifted.id)

      u1 must beSome.which((u: User) => u.id.toString == id) and
        beSome.which((u: User) => u.auth.snids == Map()) and
        beSome.which((u: User) => u.auth.session == Some(sessid))
      u2 must beSome.which((u: User) => u.id.toString == id) and
        beSome.which((u: User) => u.auth.snids == Map()) and
        beSome.which((u: User) => u.auth.session == Some(newsessid))
    }

    "Delete user in DB" in new WithApplication(appWithTestDatabase) {
      val userid = "id to test delete"

      db.user.create(User(userid))
      db.user.readById(userid)
      db.user.delete(userid)
      val u = db.user.readById(userid)

      u must beNone
    }

    "List all users in DB" in new WithApplication(appWithTestDatabase) {
      val userid = "id to test all"

      db.user.create(User(userid))
      val all = List.empty ++ db.user.all

      all.map(_.id) must contain(userid)
    }

    "One more check for listing and deleting everything" in new WithApplication(appWithTestDatabase) {
      db.user.all.foreach((u: User) => db.user.delete(u.id))

      val all = List.empty ++ db.user.all

      all must haveSize(0)
    }

    "Delete user what do not exists" in new WithApplication(appWithTestDatabase) {
      db.user.delete("Id of user who never existed in the database")

    }

    """Return "None" in search for not existing user""" in new WithApplication(appWithTestDatabase) {
      val u = db.user.readBySessionId("Another id of another never existign user")
      u must beNone
    }

// TODO: TAGS: clean me up.
//    "takeQuest must remember quest's theme in history" in new WithApplication(appWithTestDatabase) {
//      val userId = "takeQuest2"
//      val themeId = "tid"
//
//      db.user.create(User(userId))
//
//      db.user.takeQuest(
//        userId,
//        QuestInfoWithID(
//          "q",
//          QuestInfo(
//            authorId = "authorId",
//            themeId = themeId,
//            content = QuestInfoContent(
//              media = ContentReference(
//                contentType = ContentType.Photo,
//                storage = "",
//                reference = ""),
//              icon = None,
//              description = ""),
//            vip = false)),
//        new Date(),
//        new Date())
//
//      val ou = db.user.readById(userId)
//      ou must beSome.which((u: User) => u.id.toString == userId)
//      ou must beSome.which((u: User) => u.history.themesOfSelectedQuests.contains(themeId))
//    }

    "incTask should increase number of times task was completed by one" in new WithApplication(appWithTestDatabase) {
      val userid = "incTasks"
      db.user.create(User(userid))

      val tasks = DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.Client,
            description = "",
            requiredCount = 10),
          Task(
            taskType = TaskType.GiveRewards,
            description = "",
            requiredCount = 10)))

      db.user.resetTasks(userid, tasks, new Date())

      db.user.incTask(userid, TaskType.Client.toString, 0.4f, rewardReceived = true)
      db.user.incTask(userid, TaskType.GiveRewards.toString, 0.4f, rewardReceived = true)
      db.user.incTask(userid, TaskType.GiveRewards.toString, 0.4f, rewardReceived = true)

      val ou = db.user.readById(userid)
      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which((u: User) => u.profile.dailyTasks.tasks.filter(_.taskType == TaskType.Client)(0).currentCount == 1)
      ou must beSome.which((u: User) => u.profile.dailyTasks.tasks.filter(_.taskType == TaskType.GiveRewards)(0).currentCount == 2)
    }

    "incTask should change percentage completed" in new WithApplication(appWithTestDatabase) {
      val userid = "incTasks2"
      db.user.create(User(userid))

      val tasks = DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.Client,
            description = "",
            requiredCount = 10)))

      db.user.resetTasks(userid, tasks, new Date())

      db.user.incTask(userid, TaskType.Client.toString, 0.4f, rewardReceived = true)

      val ou = db.user.readById(userid)
      ou must beSome.which((u: User) => u.id.toString == userid)
      ou.get.profile.dailyTasks.tasks(0).currentCount must beEqualTo(1)
      ou.get.profile.dailyTasks.completed must beEqualTo(0.4f)
      ou.get.profile.dailyTasks.rewardReceived must beEqualTo(true)
    }

    "incTutorialTask should increase number of times task was completed by one" in new WithApplication(appWithTestDatabase) {
      val userid = "incTutorialTasks"
      val taskId = "tid"
      db.user.create(User(userid))

      val tasks = DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.AddToFollowing,
            description = "",
            requiredCount = 10),
          Task(
            taskType = TaskType.GiveRewards,
            description = "",
            requiredCount = 10),
          Task(
            taskType = TaskType.Client,
            description = "",
            requiredCount = 10,
            tutorialTask = Some(TutorialTask(
              id = taskId,
              taskType = TaskType.Client,
              description = "",
              requiredCount = 10,
              reward = Assets())))))

      db.user.resetTasks(userid, tasks, new Date())

      db.user.incTutorialTask(userid, taskId, 0.4f, rewardReceived = true)

      val ou = db.user.readById(userid)
      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which((u: User) => u.profile.dailyTasks.tasks.filter(_.taskType == TaskType.Client)(0).currentCount == 1)
      ou must beSome.which((u: User) => u.profile.dailyTasks.tasks.filter(_.taskType == TaskType.GiveRewards)(0).currentCount == 0)
      ou must beSome.which((u: User) => u.profile.dailyTasks.tasks.filter(_.taskType == TaskType.AddToFollowing)(0).currentCount == 0)
    }

    "updateQuestCreationCoolDown should reset cool down" in new WithApplication(appWithTestDatabase) {
      val userId = "resetQuestProposal"
      val date = new Date(1000)
      val dateNew = new Date(2000)

      db.user.clear()

      db.user.create(User(
        id = userId,
        profile = Profile(
          questCreationContext = QuestCreationContext(
            questCreationCoolDown = date))))

      val ou = db.user.updateQuestCreationCoolDown(userId, dateNew)

      ou must beSome.which((u: User) => u.id.toString == userId)
      ou must beSome.which((u: User) => u.profile.questCreationContext.questCreationCoolDown == dateNew)
    }

    // TODO: TAGS: clean me up.
//    "resetTodayReviewedThemes do its work" in new WithApplication(appWithTestDatabase) {
//      val userId = "resetTodayReviewedThemes"
//      val date = new Date(1000)
//
//      db.user.delete(userId)
//      db.user.create(User(
//        id = userId,
//        profile = Profile(
//          questProposalContext = QuestProposalConext(
//            todayReviewedThemeIds = List("lala")))))
//
//      val ou = db.user.resetTodayReviewedThemes(userId)
//
//      ou must beSome.which((u: User) => u.id.toString == userId)
//      ou must beSome.which((u: User) => u.profile.questProposalContext.todayReviewedThemeIds == List.empty)
//    }

    "addTasks works" in new WithApplication(appWithTestDatabase) {

      def t = {
        Task(TaskType.GiveRewards, "d", 1)
      }

      val userid = "addTasksTest"

      db.user.delete(userid)
      db.user.create(User(
        id = userid,
        profile = Profile(
          dailyTasks = DailyTasks(
            tasks = List(t, t, t),
            reward = Assets(1, 2, 3)))))

      val ou = db.user.addTasks(userid, List(t, t), Assets(100, 200, 300))

      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which((u: User) => u.profile.dailyTasks.tasks.length == 5)
      ou must beSome.which((u: User) => u.profile.dailyTasks.reward == Assets(101, 202, 303))
    }

    "addTutorialTaskAssigned works" in new WithApplication(appWithTestDatabase) {

      def t = {
        Task(TaskType.GiveRewards, "d", 1)
      }

      val userid = "addTasksTest"

      db.user.delete(userid)
      db.user.create(User(
        id = userid,
        tutorial = TutorialState(
          assignedTutorialTaskIds = List.empty)))

      db.user.addTutorialTaskAssigned(userid, "t1")
      db.user.addTutorialTaskAssigned(userid, "t2")
      db.user.addTutorialTaskAssigned(userid, "t3")
      val ou = db.user.addTutorialTaskAssigned(userid, "t2")

      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which((u: User) => u.tutorial.assignedTutorialTaskIds.length == 3)
    }

    "updateCultureId works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val userid = "updateCultureId"

      db.user.delete(userid)
      db.user.create(User(
        id = userid))

      private val cultureId: String = "cult"
      val ou = db.user.updateCultureId(userid, cultureId)

      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which((u: User) => u.demo.cultureId == Some(cultureId))
    }

    "replaceCultureIds works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val userid1 = "replaceCultureIds1"
      val userid2 = "replaceCultureIds2"
      val userid3 = "replaceCultureIds3"

      val oldC = "oldC"
      val newC = "newC"

      db.user.create(User(
        id = userid1,
        demo = UserDemographics(cultureId = Some(oldC))))

      db.user.create(User(
        id = userid2,
        demo = UserDemographics(cultureId = Some(oldC))))

      db.user.create(User(
        id = userid3,
        demo = UserDemographics(cultureId = Some(oldC + "1"))))

      db.user.replaceCultureIds(oldC, newC)

      val ou1 = db.user.readById(userid1)
      ou1 must beSome.which((u: User) => u.id.toString == userid1)
      ou1 must beSome.which((u: User) => u.demo.cultureId == Some(newC))

      val ou2 = db.user.readById(userid2)
      ou2 must beSome.which((u: User) => u.id.toString == userid2)
      ou2 must beSome.which((u: User) => u.demo.cultureId == Some(newC))

      val ou3 = db.user.readById(userid3)
      ou3 must beSome.which((u: User) => u.id.toString == userid3)
      ou3 must beSome.which((u: User) => u.demo.cultureId == Some(oldC + "1"))
    }

    "populate mustVoteSolutions list" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val userids = List("replaceCultureIds1", "replaceCultureIds2", "replaceCultureIds3")
      val solIds = List("solId0", "solId1")

      userids.foreach(v => db.user.create(User(id = v)))


      db.user.populateMustVoteSolutionsList(userids, solIds(0))
      db.user.populateMustVoteSolutionsList(userids.tail, solIds(1))

      val ou1 = db.user.readById(userids(0))
      ou1 must beSome.which((u: User) => u.mustVoteSolutions == List(solIds(0)))

      val ou2 = db.user.readById(userids(1))
      ou2 must beSome.which((u: User) => solIds forall u.mustVoteSolutions.contains)

      val ou3 = db.user.readById(userids(1))
      ou3 must beSome.which((u: User) => solIds forall u.mustVoteSolutions.contains)
    }

    "removeMustVoteSolution removes it actually" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val sol = "solid"
      val u = User(id = "idid", mustVoteSolutions = List(sol))

      db.user.create(u)
      db.user.removeMustVoteSolution(u.id, sol)

      val ou1 = db.user.readById(u.id)
      ou1 must beSome//.which((u: User) => u.mustVoteSolutions == List.empty)
    }

    "recordQuestVote works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val u = createUserStub()
      val q = createQuestStub()

      db.user.create(u)
      db.user.recordQuestVote(u.id, q.id, ContentVote.Cheating)

      val ou1 = db.user.readById(u.id)
      ou1 must beSome.which((u: User) => u.stats.votedQuests.get(q.id) == Some(ContentVote.Cheating))
    }

    "recordSolutionVote works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val u = createUserStub()
      val s = createSolutionStub()

      db.user.create(u)
      db.user.recordSolutionVote(u.id, s.id, ContentVote.Cheating)

      val ou1 = db.user.readById(u.id)
      ou1 must beSome.which((u: User) => u.stats.votedSolutions.get(s.id) == Some(ContentVote.Cheating))
    }

    "Add entry to time line" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val u = User(id = "idid_for_time_line")
      val tle1 = TimeLineEntry(
        id = "id",
        reason = TimeLineReason.Created,
        actorId = u.id,
        TimeLineType.Quest,
        objectId = "oid")

      db.user.create(u)
      db.user.addEntryToTimeLine(u.id, tle1)

      val ou1 = db.user.readById(u.id)
      ou1 must beSome[User]
      ou1.get.timeLine must beEqualTo(List(tle1))
    }

    "Add entry to time line multi" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val u = List(User(), User())
      val tle = TimeLineEntry(
        id = "id",
        reason = TimeLineReason.Created,
        actorId = u(0).id,
        TimeLineType.Quest,
        objectId = "oid")

      u.foreach(db.user.create)
      db.user.addEntryToTimeLineMulti(u.map(_.id), tle)

      val ou1 = db.user.readById(u(0).id)
      ou1 must beSome[User]
      ou1.get.timeLine must beEqualTo(List(tle))

      val ou2 = db.user.readById(u(1).id)
      ou2 must beSome[User]
      ou2.get.timeLine must beEqualTo(List(tle))
    }

    "Remove entry from time line" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val userId = "userId"
      val tle1 = TimeLineEntry(
        id = "idqwe",
        reason = TimeLineReason.Created,
        actorId = userId,
        TimeLineType.Quest,
        objectId = "oid")
      val u = User(id = userId, timeLine = List(tle1))

      db.user.create(u)
      db.user.removeEntryFromTimeLineByObjectId(u.id, tle1.objectId)

      val ou1 = db.user.readById(u.id)
      ou1 must beSome[User]
      ou1.get.timeLine must beEqualTo(List.empty)
    }


    "recordQuestSolving do its work" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val questId = "qiq"
      val user = createUserStub(questBookmark = Some(questId))
      db.user.create(user)

      db.user.recordQuestSolving(user.id, questId, removeBookmark = true)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User]
      ou1.get.stats.solvedQuests must beEqualTo(List(questId))
      ou1.get.profile.questSolutionContext.bookmarkedQuest must beNone
    }

    "setQuestBookmark do its work" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val questId = "qiq"
      val qi = QuestView(
        questId,
        createQuestStub(id = questId).info)
      val user = createUserStub(questBookmark = None)
      db.user.create(user)

      db.user.setQuestBookmark(user.id, qi)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User]
      ou1.get.profile.questSolutionContext.bookmarkedQuest must beEqualTo(Some(qi))
    }

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

    "setTimeLinePopulationTime sets it" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val time = new Date(1000)

      val user = createUserStub()

      db.user.create(user)
      db.user.setTimeLinePopulationTime(user.id, time)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User]
      val u = ou1.get
      u.schedules.timeLine must beEqualTo(time)
    }
  }
}

/**
 * Spec with another component setup for testing
 */
class UserDAOFailSpecs extends Specification
  with MongoDatabaseForTestComponent {

  /*
   * Initializing components. It's lazy to let app start first and bring up db driver.
   */
  lazy val db = new MongoDatabaseForTest
  val appWithTestDatabase = FakeApplication(additionalConfiguration = testMongoDatabase())

  def testMongoDatabase(name: String = "default"): Map[String, String] = {
    val dbname: String = "questdb-test"
    Map(
      "mongodb." + name + ".db" -> dbname)
  }

  "Mongo User DAO" should {
    "Throw StoreException in case of underlaying error" in new WithApplication(appWithTestDatabase) {
      db.user.create(User("tutumc")) must throwA[DatabaseException]
    }
  }
}

