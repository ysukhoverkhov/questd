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
    solveCost: Assets = Assets(0, 0, 0)) = {

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
          solveCost = solveCost),
      rating = QuestRating(points = points),
      status = status)
  }

  def createSolutionInfoContent = {
    QuestSolutionInfoContent(
      createContentReferenceStub,
      None)
  }

  // TODO: replace themeId with tags.
  def createSolutionStub(
    id: String = "sol id",
    cultureId: String = "cultureId",
    userId: String = "uid",
    questId: String = "qid",
    themeId: String = "themeId",
    status: QuestSolutionStatus.Value = QuestSolutionStatus.OnVoting,
    level: Int = 1,
    points: Int = 0,
    vip: Boolean = false,
    voteEndDate: Date = new Date((new Date).getTime + 100000),
    lastModDate: Date = new Date((new Date).getTime + 100000)) = {

    QuestSolution(
      id = id,
      cultureId = cultureId,
      questLevel = level,
      info = QuestSolutionInfo(
        content = createSolutionInfoContent,
        vip = vip,
        authorId = userId,
        questId = questId),
      status = status,
      rating = QuestSolutionRating(
        pointsRandom = points),
      voteEndDate = voteEndDate,
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

  def createUserStub(
    id: String = "uid",
    cultureId: String = "cultureId",
    vip: Boolean = false,
    friends: List[Friendship] = List(),
    assets: Assets = Assets(1000000, 1000000, 1000000),
    mustVoteSolutions: List[String] = List(),
    level: Int = 18,
    questCreationCoolDown: Date = new Date(Long.MaxValue),
    takenTheme: Option[ThemeInfoWithID] = Some(ThemeInfoWithID("theme_id", createThemeStub().info)),
    rights: Rights = Rights.full,
    timeLine: List[TimeLineEntry] = List(),
    questBookmark: Option[String] = None) = {

    User(
      id = id,
      demo = UserDemographics(
        cultureId = Some(cultureId)),
      privateDailyResults = List(DailyResult(
        startOfPeriod = new Date(),
        dailySalary = Assets())),
      profile = Profile(
        assets = assets,
        ratingToNextLevel = 100000,
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
      timeLine = timeLine)
  }

}
