package controllers.domain.app.user

import controllers.domain.BaseAPISpecs
import models.domain._
import models.domain.view._
import java.util.Date
import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.OkApiResult
import org.mockito.Matchers

class SolveQuestAPISpecs extends BaseAPISpecs {

  def createUser(userId: String = "userid", vip: Boolean = false) = {
    User(
      id = userId,
      privateDailyResults = List(DailyResult(
        startOfPeriod = new Date(),
        dailyAssetsDecrease = Assets())),
      profile = Profile(
        questSolutionContext = QuestSolutionContext(
          takenQuest = Some(QuestInfoWithID("quest_id", QuestInfo(themeId = "theme_id", vip = false, content = QuestInfoContent(ContentReference(ContentType.Photo.toString(), "", ""), None, "")))),
          questDeadline = new Date(Long.MaxValue)),
        publicProfile = PublicProfile(vip = vip),
        rights = Rights.full))
  }

  def createSolutionInfoContent = {
    QuestSolutionInfoContent(ContentReference(ContentType.Photo.toString(), "", ""), None)
  }

  def createSolution(
    solutionId: String,
    userId: String,
    questId: String,
    status: String = QuestSolutionStatus.WaitingForCompetitor.toString,
    questLevel: Int = 1,
    themeId: String = "tid",
    points: Int = 0) = {

    QuestSolution(
      id = solutionId,
      userId = userId,
      questLevel = questLevel,
      info = QuestSolutionInfo(
        content = createSolutionInfoContent,
        vip = true,
        themeId = themeId,
        questId = questId),
      status = status,
      rating = QuestSolutionRating(
        pointsRandom = points),
      voteEndDate = new Date((new Date).getTime() + 100000))
  }

  def createQuest(id: String) = {
    Quest(
      id = id,
      authorUserId = "aid",
      approveReward = Assets(1, 2, 3),
      info = QuestInfo(
        themeId = "tid",
        vip = false,
        content = QuestInfoContent(
          media = ContentReference(
            contentType = "type",
            storage = "la",
            reference = "tu"),
          icon = None,
          description = "desc")))
  }

  "Solve Quest API" should {

    "Create regular solution for regular users" in context {

      val u = createUser(vip = false)
      val s = createSolutionInfoContent

      user.resetQuestSolution(any, any) returns Some(u)

      val result = api.proposeSolution(ProposeSolutionRequest(u, s))

      result.body.get.allowed must beEqualTo(ProfileModificationResult.OK)

      there was one(solution).create(
        QuestSolution(
          id = anyString,
          userId = u.id,
          questLevel = u.profile.questSolutionContext.takenQuest.get.obj.level,
          info = QuestSolutionInfo(
            content = s,
            themeId = u.profile.questSolutionContext.takenQuest.get.obj.themeId,
            questId = u.profile.questSolutionContext.takenQuest.get.id,
            vip = false),
          voteEndDate = new Date()))
    }

    "Create VIP solution for VIP users" in context {

      val u = createUser(vip = true)
      val s = createSolutionInfoContent

      user.resetQuestSolution(any, any) returns Some(u)

      val result = api.proposeSolution(ProposeSolutionRequest(u, s))

      result.body.get.allowed must beEqualTo(ProfileModificationResult.OK)

      there was one(solution).create(
        QuestSolution(
          id = anyString,
          userId = u.id,
          questLevel = u.profile.questSolutionContext.takenQuest.get.obj.level,
          info = QuestSolutionInfo(
            content = s,
            themeId = u.profile.questSolutionContext.takenQuest.get.obj.themeId,
            questId = u.profile.questSolutionContext.takenQuest.get.id,
            vip = true),
          voteEndDate = new Date()))
    }

    "Do not fight with himself in quest" in context {

      val user1 = createUser("user1")
      val mySolution = createSolution("solId1", user1.id, "qid")

      solution.allWithParams(
        status = List(QuestSolutionStatus.WaitingForCompetitor.toString),
        questIds = List(mySolution.info.questId)) returns List(mySolution).iterator

      val result = api.tryFightQuest(TryFightQuestRequest(mySolution))

      result must beEqualTo(OkApiResult(Some(TryFightQuestResult())))

      there were no(solution).updateStatus(any, any, any)
      there were no(user).storeSolutionInDailyResult(any, any)
    }

    "Receive reward for winning quest battle" in context {

      val quest = createQuest("qid")
      val user1 = createUser("user1")
      val mySolution = createSolution("solId1", user1.id, quest.id, points = 1)
      val user2 = createUser("user2")
      val rivalSolution = createSolution("solId2", user2.id, quest.id, points = 0)

      solution.allWithParams(
        status = List(QuestSolutionStatus.WaitingForCompetitor.toString),
        questIds = List(mySolution.info.questId)) returns List(mySolution, rivalSolution).iterator

      solution.updateStatus(mySolution.id, QuestSolutionStatus.Won.toString, Some(rivalSolution.id)) returns Some(mySolution.copy(status = QuestSolutionStatus.Won.toString))
      solution.updateStatus(rivalSolution.id, QuestSolutionStatus.Lost.toString, Some(mySolution.id)) returns Some(rivalSolution.copy(status = QuestSolutionStatus.Lost.toString))

      user.readById(mySolution.userId) returns Some(user1)
      user.readById(rivalSolution.userId) returns Some(user2)

      db.quest.readById(quest.id) returns Some(quest)

      db.user.storeSolutionInDailyResult(Matchers.eq(user1.id), any) returns Some(user1)
      db.user.storeSolutionInDailyResult(Matchers.eq(user2.id), any) returns Some(user2)

      val result = api.tryFightQuest(TryFightQuestRequest(mySolution))

      result must beEqualTo(OkApiResult(Some(TryFightQuestResult())))

      there was
        one(solution).updateStatus(mySolution.id, QuestSolutionStatus.Won.toString, Some(rivalSolution.id)) andThen
        one(solution).updateStatus(rivalSolution.id, QuestSolutionStatus.Lost.toString, Some(mySolution.id))
      there were two(user).readById(any)
      there were two(user).storeSolutionInDailyResult(any, any)
    }

  }

}