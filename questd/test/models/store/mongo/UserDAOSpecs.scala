

package models.store.mongo

import java.util.Date

import models.domain.common.{ClientPlatform, Assets, ContentVote}
import models.domain.user._
import models.domain.user.auth.{AuthInfo, CrossPromotedApp, LoginMethod}
import models.domain.user.battlerequests.{BattleRequestStatus, BattleRequest}
import models.domain.user.dailyresults.BattleResult
import models.domain.user.demo.UserDemographics
import models.domain.user.devices.Device
import models.domain.user.friends.{ReferralStatus, FriendshipStatus, Friendship}
import models.domain.user.message.MessageInformation
import models.domain.user.profile._
import models.domain.user.stats.SolutionsInBattle
import models.domain.user.timeline.{TimeLineType, TimeLineReason, TimeLineEntry}
import models.store._
import models.view.QuestView
import org.specs2.mutable._
import play.api.test._
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
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
      db.user.create(User(user_id, AuthInfo(loginMethods = List(LoginMethod("FB", fbid)))))
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
        beSome.which((u: User) => u.auth.loginMethods == List.empty) and
        beSome.which((u: User) => u.auth.session.contains(sessid))
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
        beSome.which((u: User) => u.auth.loginMethods == List.empty) and
        beSome.which((u: User) => u.auth.session.contains(sessid))
      u2 must beSome.which((u: User) => u.id.toString == id) and
        beSome.which((u: User) => u.auth.loginMethods == List.empty) and
        beSome.which((u: User) => u.auth.session.contains(newsessid))
    }

    "Delete user in DB" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

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
      val u = db.user.readBySessionId("Another id of another never existing user")
      u must beNone
    }

    "Add cross promotions" in new WithApplication(appWithTestDatabase) {
      val user: User = createUserStub()
      db.user.create(user)

      val u = db.user.addCrossPromotions(user.id, "FB", List(CrossPromotedApp("appName", "userId"), CrossPromotedApp("appName2", "userId2")))

      u must beSome
      val u2 = u.get
      u2.auth.loginMethods.find(_.methodName == "FB").get.crossPromotion.apps.length must beEqualTo(2)
      u2.auth.loginMethods.find(_.methodName == "FB").get.crossPromotion.apps.find(_.appName == "appName") must beSome
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
            taskType = TaskType.Custom,
            description = "",
            requiredCount = 10),
          Task(
            taskType = TaskType.GiveRewards,
            description = "",
            requiredCount = 10)))

      db.user.resetTasks(userid, tasks, new Date())

      db.user.incTask(id = userid, taskId = tasks.tasks.head.id)
      db.user.incTask(id = userid, taskId = tasks.tasks(1).id)
      db.user.incTask(id = userid, taskId = tasks.tasks(1).id)

      val ou = db.user.readById(userid)
      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which((u: User) => u.profile.dailyTasks.tasks.filter(_.taskType == TaskType.Custom).head.currentCount == 1)
      ou must beSome.which((u: User) => u.profile.dailyTasks.tasks.filter(_.taskType == TaskType.GiveRewards).head.currentCount == 2)
    }

    "setTasksCompletedFraction should change percentage completed" in new WithApplication(appWithTestDatabase) {
      val userid = "incTasks2"
      val fraction = 0.3f
      db.user.create(User(userid))

      val tasks = DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.Custom,
            description = "",
            requiredCount = 10)))

      db.user.resetTasks(userid, tasks, new Date())

      db.user.setTasksCompletedFraction(userid, fraction)

      val ou = db.user.readById(userid)
      ou must beSome.which((u: User) => u.id.toString == userid)
      ou.get.profile.dailyTasks.completed must beEqualTo(fraction)
      ou.get.profile.dailyTasks.rewardReceived must beEqualTo(false)
    }

    "setTasksRewardReceived should change the flag" in new WithApplication(appWithTestDatabase) {
      val userid = "incTasks2"
      db.user.create(User(userid))

      val tasks = DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.Custom,
            description = "",
            requiredCount = 10)))

      db.user.resetTasks(userid, tasks, new Date())

      db.user.setTasksRewardReceived(id = userid, rewardReceived = true)

      val ou = db.user.readById(userid)
      ou must beSome.which((u: User) => u.id.toString == userid)
      ou.get.profile.dailyTasks.rewardReceived must beEqualTo(true)
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
        Task(taskType = TaskType.GiveRewards, description = "d", requiredCount = 1)
      }

      val userid = "addTasksTest"

      db.user.delete(userid)
      db.user.create(User(
        id = userid,
        profile = Profile(
          dailyTasks = DailyTasks(
            tasks = List(t, t, t),
            reward = Assets(1, 2, 3)))))

      val ou = db.user.addTasks(userid, List(t, t), Some(Assets(rating = 10)))

      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which((u: User) => u.profile.dailyTasks.tasks.length == 5)
      ou must beSome.which((u: User) => u.profile.dailyTasks.reward == Assets(1, 2, 13))
    }

    "addClosedTutorialElement works" in new WithApplication(appWithTestDatabase) {

      val userid = "addTasksTest"
      val elementid = "elementid"

      db.user.delete(userid)
      db.user.create(User(
        id = userid))

      val ou = db.user.addClosedTutorialElement(userid, ClientPlatform.iPhone.toString, elementid)

      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which((u: User) => u.profile.tutorialStates(ClientPlatform.iPhone.toString).closedElementIds == List(elementid))
    }

    "addTutorialTaskAssigned works" in new WithApplication(appWithTestDatabase) {

      val userid = "addTasksTest"

      db.user.delete(userid)
      db.user.create(User(
        id = userid))

      db.user.addTutorialTaskAssigned(userid, ClientPlatform.iPhone.toString, "t1")
      db.user.addTutorialTaskAssigned(userid, ClientPlatform.iPhone.toString, "t2")
      db.user.addTutorialTaskAssigned(userid, ClientPlatform.iPhone.toString, "t3")
      val ou = db.user.addTutorialTaskAssigned(userid, ClientPlatform.iPhone.toString, "t2")

      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which((u: User) => u.profile.tutorialStates(ClientPlatform.iPhone.toString).usedTutorialTaskIds.length == 3)
    }

    "addTutorialQuestAssigned works" in new WithApplication(appWithTestDatabase) {

      val userid = "addQuestsTest"

      db.user.delete(userid)
      db.user.create(User(
        id = userid))

      db.user.addTutorialQuestAssigned(userid, ClientPlatform.iPhone.toString, "t1")
      db.user.addTutorialQuestAssigned(userid, ClientPlatform.iPhone.toString, "t2")
      db.user.addTutorialQuestAssigned(userid, ClientPlatform.iPhone.toString, "t3")
      val ou = db.user.addTutorialQuestAssigned(userid, ClientPlatform.iPhone.toString, "t2")

      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which((u: User) => u.profile.tutorialStates(ClientPlatform.iPhone.toString).usedTutorialQuestIds.length == 3)
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
      ou must beSome.which((u: User) => u.demo.cultureId.contains(cultureId))
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
      ou1 must beSome.which((u: User) => u.demo.cultureId.contains(newC))

      val ou2 = db.user.readById(userid2)
      ou2 must beSome.which((u: User) => u.id.toString == userid2)
      ou2 must beSome.which((u: User) => u.demo.cultureId.contains(newC))

      val ou3 = db.user.readById(userid3)
      ou3 must beSome.which((u: User) => u.id.toString == userid3)
      ou3 must beSome.which((u: User) => u.demo.cultureId.contains(oldC + "1"))
    }

    "populate mustVoteSolutions list" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val userids = List("replaceCultureIds1", "replaceCultureIds2", "replaceCultureIds3")
      val solIds = List("solId0", "solId1")

      userids.foreach(v => db.user.create(User(id = v)))


      db.user.populateMustVoteSolutionsList(userids, solIds.head)
      db.user.populateMustVoteSolutionsList(userids.tail, solIds(1))

      val ou1 = db.user.readById(userids.head)
      ou1 must beSome.which((u: User) => u.mustVoteSolutions == List(solIds.head))

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
        actorId = u.head.id,
        TimeLineType.Quest,
        objectId = "oid")

      u.foreach(db.user.create)
      db.user.addEntryToTimeLineMulti(u.map(_.id), tle)

      val ou1 = db.user.readById(u.head.id)
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
      val solutionId = "siq"
      val user = createUserStub(questBookmark = Some(questId))
      db.user.create(user)

      db.user.recordQuestSolving(user.id, questId, solutionId, removeBookmark = true)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User]
      ou1.get.stats.solvedQuests must beEqualTo(Map(questId -> solutionId))
      ou1.get.profile.questSolutionContext.bookmarkedQuest must beNone
    }

    "setQuestBookmark do its work" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val questId = "qiq"
      val qi = QuestView(
        questId,
        createQuestStub(id = questId).info,
        None,
        None)
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

    "addMessage adds a message" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val m = message.MessageAllTasksCompleted().toMessage

      db.user.create(user)
      db.user.addMessage(user.id, m)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User].which(_.profile.messages == List(m))
    }

    "addMessageToEveryone does its work" in new WithApplication(appWithTestDatabase) {
      val users = (1 to 5).map(i => createUserStub())

      users.foreach(db.user.create)
      db.user.addMessageToEveryone(MessageInformation("", Some("")))

      val u: Iterator[User] = db.user.all
      val u2 = u.toList
      u2(0).profile.messages.length must beEqualTo(1)
      u2(1).profile.messages.length must beEqualTo(1)
      u2(2).profile.messages.length must beEqualTo(1)
      u2(3).profile.messages.length must beEqualTo(1)
      u2(4).profile.messages.length must beEqualTo(1)
    }

    "removeMessage removes a message" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val m = message.MessageAllTasksCompleted().toMessage

      db.user.create(user)
      db.user.addMessage(user.id, m)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User].which(_.profile.messages == List(m))

      db.user.removeMessage(user.id, m.id)

      val ou2 = db.user.readById(user.id)
      ou2 must beSome[User].which(_.profile.messages == List.empty)
    }

    "removeOldestMessage removes a really oldest message" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val ms = (1 to 5).map(n => message.MessageAllTasksCompleted().toMessage)

      db.user.create(user)
      ms.foreach(db.user.addMessage(user.id, _))

      db.user.removeOldestMessage(user.id)

      val ou2 = db.user.readById(user.id)
      ou2 must beSome[User].which(_.profile.messages == ms.tail)
    }

    "addDevice and remove device works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val device = Device(ClientPlatform.iPhone, "token")

      db.user.create(user)
      db.user.addDevice(user.id, device)

      val ou1 = db.user.readById(user.id)
      ou1 must beSome[User].which(u => u.devices.size == 1 && u.devices.head == device)

      db.user.removeDevice(user.id, device.token)

      val ou2 = db.user.readById(user.id)
      ou2 must beSome[User].which(u => u.devices.isEmpty)
    }

    "removeDevice works" in new WithApplication(appWithTestDatabase) {
      success
    }

    "Setting friendship works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val friend = createUserStub()
      val fs = Friendship(friend.id, FriendshipStatus.Invited)

      db.user.create(user)
      db.user.addFriendship(user.id, fs)
      db.user.updateFriendship(user.id, friend.id, Some(FriendshipStatus.Accepted.toString), Some(ReferralStatus.ReferredBy.toString))

      val ou2 = db.user.readById(user.id)
      ou2 must beSome[User]
      val u = ou2.get
      u.friends.head.status must beEqualTo(FriendshipStatus.Accepted)
      u.friends.head.referralStatus must beEqualTo(ReferralStatus.ReferredBy)
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

    "addBattleRequest adds it" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val br = BattleRequest("1", "2", "3", BattleRequestStatus.Accepted)

      db.user.create(user)
      db.user.addBattleRequest(user.id, br)

      val ou = db.user.readById(user.id)

      ou must beSome
      ou.get.battleRequests.head must beEqualTo(br)
    }

    "updateBattleRequest works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub()
      val br = BattleRequest("1", "2", "3", BattleRequestStatus.Accepted)

      db.user.create(user)
      db.user.addBattleRequest(user.id, br)
      db.user.updateBattleRequest(user.id, "2", "3", BattleRequestStatus.Rejected.toString)

      val ou = db.user.readById(user.id)

      ou must beSome
      ou.get.battleRequests.head must beEqualTo(br.copy(status = BattleRequestStatus.Rejected))
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

