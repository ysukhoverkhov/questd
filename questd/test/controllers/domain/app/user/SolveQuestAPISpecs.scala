package controllers.domain.app.user

import controllers.domain.BaseAPISpecs
import models.domain._
import models.domain.base._
import java.util.Date
import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.OkApiResult

class SolveQuestAPISpecs extends BaseAPISpecs {

  def createUser(userId: String = "userid", vip: Boolean = false) = {
    User(
      id = userId,
      privateDailyResults = List(DailyResult(
        startOfPeriod = new Date(),
        dailyAssetsDecrease = Assets())),
      profile = Profile(
        questSolutionContext = QuestSolutionContext(
          takenQuest = Some(QuestInfoWithID("quest_id", QuestInfo(themeId = "theme_id", vip = false, content = QuestInfoContent(ContentReference(ContentType.Photo, "", ""), None, "")))),
          questDeadline = new Date(Long.MaxValue)),
        publicProfile = PublicProfile(vip = vip),
        rights = Rights.full))
  }

  def createSolutionInfoContent = {
    QuestSolutionInfoContent(ContentReference(ContentType.Photo, "", ""), None)
  }

  def createSolution(
    solutionId: String,
    userId: String,
    questId: String,
    status: QuestSolutionStatus.Value = QuestSolutionStatus.WaitingForCompetitor,
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
      voteEndDate = new Date())
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
            contentType = ContentType.Photo,
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
          voteEndDate = any))
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
          voteEndDate = any))
    }

    "Do not fight with himself in quest" in context {

      val user1 = createUser("user1")
      val mySolution = createSolution("solId1", user1.id, "qid")

      solution.allWithParams(
        status = Some(QuestSolutionStatus.WaitingForCompetitor.toString),
        questIds = List(mySolution.info.questId)) returns List(mySolution).iterator

      val result = api.tryFightQuest(TryFightQuestRequest(mySolution))

      result must beEqualTo(OkApiResult(Some(TryFightQuestResult())))

      there were no(solution).updateStatus(any, any)
      there were no(user).storeSolutionInDailyResult(any, any)
    }

    "Receive reward for winning quest battle" in context {

      val quest = createQuest("qid")
      val user1 = createUser("user1")
      val mySolution = createSolution("solId1", user1.id, quest.id, points = 1)
      val user2 = createUser("user2")
      val rivalSolution = createSolution("solId2", user2.id, quest.id, points = 0)

      solution.allWithParams(
        status = Some(QuestSolutionStatus.WaitingForCompetitor.toString),
        questIds = List(mySolution.info.questId)) returns List(mySolution, rivalSolution).iterator

      solution.updateStatus(mySolution.id, QuestSolutionStatus.Won.toString) returns Some(mySolution.copy(status = QuestSolutionStatus.Won))
      solution.updateStatus(rivalSolution.id, QuestSolutionStatus.Lost.toString) returns Some(rivalSolution.copy(status = QuestSolutionStatus.Lost))

      user.readById(mySolution.userId) returns Some(user1)
      user.readById(rivalSolution.userId) returns Some(user2)

      db.quest.readById(quest.id) returns Some(quest)

      val result = api.tryFightQuest(TryFightQuestRequest(mySolution))

      result must beEqualTo(OkApiResult(Some(TryFightQuestResult())))

      there were two(solution).updateStatus(any, any)
      there were two(user).readById(any)
      there were two(user).storeSolutionInDailyResult(any, any)
    }

  }

}