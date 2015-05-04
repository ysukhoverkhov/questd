package testhelpers

import java.util.Date
import models.domain._
import models.domain.base.ID
import models.domain.view.{QuestView, ThemeInfoWithID}


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
    points: Int = 0,
    solveCost: Assets = Assets(0, 0, 0),
    likes: Int = 0) = {

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
          solveRewardWon = Assets(),
          solveRewardLost = Assets()),
      rating = QuestRating(points = points, likesCount = likes),
      status = status)
  }

  def createSolutionInfoContent = {
    SolutionInfoContent(
      createContentReferenceStub,
      None)
  }

  // TODO: TAGS: replace themeId with tags.
  def createSolutionStub(
    id: String = "sol id",
    cultureId: String = "cultureId",
    authorId: String = "uid",
    questId: String = "qid",
    themeId: String = "themeId",
    status: SolutionStatus.Value = SolutionStatus.OnVoting,
    level: Int = 1,
    points: Int = 0,
    vip: Boolean = false,
    lastModDate: Date = new Date((new Date).getTime + 100000)) = {

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
        pointsRandom = points),
      lastModDate = lastModDate)
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
    questSolutionResult: List[QuestSolutionResult] = List.empty) = {
    DailyResult(
      startOfPeriod = startOfPeriod,
      questsIncome = questsIncome,
      decidedQuestSolutions = questSolutionResult
    )
  }

  def createBattleStub(
    id: String = ID.generateUUID(),
    solutionIds: List[String] = List("1", "2"),
    authorIds: List[String] = List("a1", "a2"),
    status: BattleStatus.Value = BattleStatus.Fighting,
    level: Int = 19,
    vip: Boolean = false,
    cultureId: String = "c1",
    winnerIds: List[String] = List("a1"),
    voteEndDate: Date = new Date()
    ) = {
    Battle(
      id = id,
      info = BattleInfo(
        solutionIds = solutionIds,
        authorIds = authorIds,
        status = status,
        voteEndDate = voteEndDate,
        winnerIds = winnerIds),
      level = level,
      vip = vip,
      cultureId = cultureId
    )
  }

  def createUserStub(
    id: String = ID.generateUUID(),
    cultureId: Option[String] = Some("cultureId"),
    vip: Boolean = false,
    friends: List[Friendship] = List.empty,
    assets: Assets = Assets(100000, 100000, 100000),
    mustVoteSolutions: List[String] = List.empty,
    level: Int = 18,
    questCreationCoolDown: Date = new Date(Long.MaxValue),
    createdQuests: List[String] = List.empty,
    createdSolutions: List[String] = List.empty,
    solvedQuests: List[String] = List.empty,
    votedQuests: Map[String, ContentVote.Value] = Map.empty,
    votedSolutions: Map[String, ContentVote.Value] = Map.empty,
    takenTheme: Option[ThemeInfoWithID] = Some(ThemeInfoWithID("theme_id", createThemeStub().info)),
    rights: Rights = Rights.full,
    timeLine: List[TimeLineEntry] = List.empty,
    questBookmark: Option[String] = None,
    privateDailyResults: List[DailyResult] = List(createDailyResultStub())) = {

    User(
      id = id,
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
        rights = rights),
      friends = friends,
      mustVoteSolutions = mustVoteSolutions,
      timeLine = timeLine,
      stats = UserStats(
        createdQuests = createdQuests,
        createdSolutions = createdSolutions,
        solvedQuests = solvedQuests,
        votedQuests = votedQuests,
        votedSolutions = votedSolutions))
  }
}
