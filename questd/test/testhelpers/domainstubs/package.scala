package testhelpers

import java.util.Date
import models.domain._
import models.domain.base.ID
import models.domain.view.{QuestInfoWithID, ThemeInfoWithID}


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

  // TODO: replace themeId with tags.
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
    reason: TimeLineReason.Value = TimeLineReason.Created,
    objectAuthorId: String = "authorId",
    objectType: TimeLineType.Value = TimeLineType.Quest,
    objectId: String = "objectId",
    ourVote: Option[ContentVote.Value] = None) = {
    TimeLineEntry(
      reason = reason,
      objectAuthorId = objectAuthorId,
      objectType = objectType,
      objectId = objectId,
      ourVote = ourVote
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
    dailySalary: Assets = Assets(),
    questsIncome: List[QuestIncome] = List(createQuestIncomeStub())
    ) = {
    DailyResult(
      startOfPeriod = startOfPeriod,
      dailySalary = dailySalary,
      questsIncome = questsIncome
    )
  }

  def createBattleStub(
    solutionIds: List[String] = List("1", "2"),
    status: BattleStatus.Value = BattleStatus.Fighting,
    voteEndDate: Date = new Date()
    ) = {
    Battle(
      info = BattleInfo(
        solutionIds = solutionIds,
        status = status,
        voteEndDate = voteEndDate)
    )
  }

  def createUserStub(
    id: String = "uid",
    cultureId: String = "cultureId",
    vip: Boolean = false,
    friends: List[Friendship] = List(),
    assets: Assets = Assets(100000, 100000, 100000),
    mustVoteSolutions: List[String] = List(),
    level: Int = 18,
    questCreationCoolDown: Date = new Date(Long.MaxValue),
    solvedQuests: List[String] = List(),
    takenTheme: Option[ThemeInfoWithID] = Some(ThemeInfoWithID("theme_id", createThemeStub().info)),
    rights: Rights = Rights.full,
    timeLine: List[TimeLineEntry] = List(),
    questBookmark: Option[String] = None,
    privateDailyResults: List[DailyResult] = List(createDailyResultStub())) = {

    User(
      id = id,
      demo = UserDemographics(
        cultureId = Some(cultureId)),
      privateDailyResults = privateDailyResults,
      profile = Profile(
        assets = assets,
        ratingToNextLevel = 1000000,
        questSolutionContext = QuestSolutionContext(
          bookmarkedQuest = questBookmark.map(QuestInfoWithID(_, createQuestStub().info))),
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
      stats = UserStats(solvedQuests = solvedQuests))
  }

}
