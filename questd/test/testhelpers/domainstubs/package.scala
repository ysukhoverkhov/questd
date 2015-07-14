package testhelpers

import java.util.Date

import models.domain.base.ID
import models.domain.battle.{Battle, BattleInfo, BattleSide, BattleStatus}
import models.domain.comment.{Comment, CommentInfo}
import models.domain.common.{Assets, ContentReference, ContentType, ContentVote}
import models.domain.quest._
import models.domain.solution._
import models.domain.tag.{Theme, ThemeInfo}
import models.domain.tutorial.TutorialPlatform
import models.domain.user._
import models.domain.user.auth.{AuthInfo, LoginMethod}
import models.domain.user.battlerequests.BattleRequest
import models.domain.user.dailyresults._
import models.domain.user.demo.UserDemographics
import models.domain.user.friends.Friendship
import models.domain.user.profile._
import models.domain.user.stats.{SolutionsInBattle, UserStats}
import models.domain.user.timeline.{TimeLineEntry, TimeLineReason, TimeLineType}
import models.view.{QuestView, ThemeInfoWithID}


package object domainstubs {

  def createContentReferenceStub = ContentReference(
    contentType = ContentType.Photo,
    storage = "",
    reference = "")

  def createThemeStub(
    id: String = ID.generateUUID(),
    cultureId: String = "cultureId",
    name: String = "name",
    desc: String = "desc") =

    Theme(
      id = id,
      cultureId = cultureId,
      info = ThemeInfo(
        media = createContentReferenceStub,
        name = name,
        description = desc))

  def createQuestStub(
    id: String = "id",
    authorId: String = "authorId",
    status: QuestStatus.Value = QuestStatus.InRotation,
    level: Int = 10,
    vip: Boolean = false,
    cultureId: String = "cultureId",
    timelinePoints: Int = 0,
    solveCost: Assets = Assets(0, 0, 0),
    solveReward: Assets = Assets(0, 0, 0),
    likes: Int = 0,
    solutionsCount: Int = 0) = {

    Quest(
      id = id,
      cultureId = cultureId,
      info = QuestInfo(
        authorId = authorId,
        vip = vip,
        level = level,
        content = QuestInfoContent(
          media = ContentReference(
            contentType = ContentType.Photo,
            storage = "la",
            reference = "tu"),
          icon = None,
          description = "desc"),
          solveCost = solveCost,
          solveReward = solveReward,
          victoryReward = Assets(1, 1, 1),
          defeatReward =Assets(2, 2, 2)),
      rating = QuestRating(timelinePoints = timelinePoints, likesCount = likes),
      status = status,
      solutionsCount = solutionsCount)
  }

  def createSolutionInfoContent = {
    SolutionInfoContent(
      createContentReferenceStub,
      None)
  }

  // TODO: TAGS: replace themeId with tags.
  def createSolutionStub(
    id: String = ID.generateUUID(),
    cultureId: String = "cultureId",
    authorId: String = "uid",
    questId: String = "qid",
    themeId: String = "themeId",
    status: SolutionStatus.Value = SolutionStatus.InRotation,
    level: Int = 1,
    timelinePoints: Int = 0,
    vip: Boolean = false,
    lastModDate: Date = new Date((new Date).getTime + 100000),
    battleIds: List[String] = List.empty) = {

    Solution(
      id = id,
      cultureId = cultureId,
      questLevel = level,
      info = SolutionInfo(
        content = createSolutionInfoContent,
        vip = vip,
        authorId = authorId,
        questId = questId),
      status = status,
      rating = SolutionRating(
        timelinePoints = timelinePoints),
      lastModDate = lastModDate,
      battleIds = battleIds)
  }

  def createTimeLineEntryStub(
    id: String = ID.generateUUID(),
    reason: TimeLineReason.Value = TimeLineReason.Created,
    actorId: String = "authorId",
    objectType: TimeLineType.Value = TimeLineType.Quest,
    objectId: String = "objectId") = {
    TimeLineEntry(
      id = id,
      reason = reason,
      actorId = actorId,
      objectType = objectType,
      objectId = objectId
    )
  }

  def createQuestIncomeStub(
    questId: String = "questId",
    passiveIncome: Assets = Assets(),
    timesLiked: Int = 0,
    likesIncome: Assets = Assets(),
    timesSolved: Int = 0,
    solutionsIncome: Assets = Assets()
    ) = {
    QuestIncome(
      questId = questId,
      passiveIncome = passiveIncome,
      timesLiked = timesLiked,
      likesIncome = likesIncome,
      timesSolved = timesSolved,
      solutionsIncome = solutionsIncome
    )
  }

  def createDailyResultStub(
    startOfPeriod: Date = new Date(),
    questsIncome: List[QuestIncome] = List(createQuestIncomeStub()),
    questResult: List[QuestResult] = List.empty,
    solutionResult: List[SolutionResult] = List.empty,
    battleResult: List[BattleResult] = List.empty) = {
    DailyResult(
      startOfPeriod = startOfPeriod,
      questsIncome = questsIncome,
      decidedQuests = questResult,
      decidedSolutions = solutionResult,
      decidedBattles = battleResult
    )
  }

  def createCommentStub(
    id: String = ID.generateUUID(),
    message: String = "the message") = {
    Comment(
      id = id,
      info = CommentInfo(
        commentedObjectId = ID.generateUUID(),
        authorId = ID.generateUUID(),
        respondedCommentId = Some(ID.generateUUID()),
        postingDate = new Date(),
        message = message
      ))
  }

  def createBattleStub(
    id: String = ID.generateUUID(),
    questId: String = "questId",
    solutionIds: List[String] = List("1", "2"),
    authorIds: List[String] = List("a1", "a2"),
    points: List[Int] = List(0, 0),
    status: BattleStatus.Value = BattleStatus.Fighting,
    level: Int = 19,
    vip: Boolean = false,
    cultureId: String = "c1",
    winnerIds: List[String] = List("a1"),
    voteEndDate: Date = new Date(),
    timelinePoints: Int = 0) = {
    Battle(
      id = id,
      info = BattleInfo(
        battleSides = (solutionIds, authorIds, points).zipped.map {
          case (s, a, p) =>
            BattleSide(
              solutionId = s,
              authorId = a,
              pointsRandom = p,
              isWinner = winnerIds.contains(a)
            )
        },
        questId = questId,
        status = status,
        voteEndDate = voteEndDate),
      level = level,
      vip = vip,
      cultureId = cultureId,
      timelinePoints = timelinePoints)
  }

  def createUserStub(
    id: String = ID.generateUUID(),
    cultureId: Option[String] = Some("cultureId"),
    vip: Boolean = false,
    friends: List[Friendship] = List.empty,
    followers: List[String] = List.empty,
    assets: Assets = Assets(100000, 100000, 100000),
    mustVoteSolutions: List[String] = List.empty,
    level: Int = 18,
    questCreationCoolDown: Date = new Date(0),
    createdQuests: List[String] = List.empty,
    solvedQuests: Map[String, String] = Map.empty,
    votedQuests: Map[String, ContentVote.Value] = Map.empty,
    votedSolutions: Map[String, ContentVote.Value] = Map.empty,
    votedBattles: Map[String, String] = Map.empty,
    participatedBattles: Map[String, String] = Map.empty,
    takenTheme: Option[ThemeInfoWithID] = Some(ThemeInfoWithID("theme_id", createThemeStub().info)),
    rights: Rights = Rights.full,
    timeLine: List[TimeLineEntry] = List.empty,
    questBookmark: Option[String] = None,
    privateDailyResults: List[DailyResult] = List(createDailyResultStub()),
    battleRequests: List[BattleRequest] = List.empty,
    tutorialState: TutorialState = TutorialState(dailyTasksSuppression = false),
    dailyTasks: DailyTasks = DailyTasks()) = {

    User(
      id = id,
      auth = AuthInfo(
        loginMethods = List(LoginMethod(
          methodName = "FB",
          userId = "adasd"
        ))),
      demo = UserDemographics(
        cultureId = cultureId),
      privateDailyResults = privateDailyResults,
      profile = Profile(
        assets = assets,
        ratingToNextLevel = 1000000,
        questSolutionContext = QuestSolutionContext(
          bookmarkedQuest = questBookmark.map(QuestView(_, createQuestStub().info, None, None))),
        questCreationContext = QuestCreationContext(
          questCreationCoolDown = questCreationCoolDown),
        publicProfile = PublicProfile(
          vip = vip,
          level = level,
          bio = Bio(
            gender = Gender.Male)),
        rights = rights,
        tutorialStates = Map(TutorialPlatform.iPhone.toString -> tutorialState),
        dailyTasks = dailyTasks),
      friends = friends,
      followers = followers,
      mustVoteSolutions = mustVoteSolutions,
      timeLine = timeLine,
      stats = UserStats(
        createdQuests = createdQuests,
        solvedQuests = solvedQuests,
        votedQuests = votedQuests,
        votedSolutions = votedSolutions,
        votedBattles = votedBattles,
        participatedBattles = participatedBattles.map{case (k, v) => (k, SolutionsInBattle(List(v)))}),
      battleRequests = battleRequests)
  }
}
