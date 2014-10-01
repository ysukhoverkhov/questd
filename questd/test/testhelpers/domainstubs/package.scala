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
    themeId: String = "themeId",
    status: QuestStatus.Value = QuestStatus.OnVoting,
    level: Int = 10,
    vip: Boolean = false,
    cultureId: String = "cultureId") = {

    Quest(
      id = id,
      cultureId = cultureId,
      approveReward = Assets(1, 2, 3),
      info = QuestInfo(
        authorId = authorId,
        themeId = themeId,
        vip = vip,
        level = level,
        content = QuestInfoContent(
          media = ContentReference(
            contentType = ContentType.Photo,
            storage = "la",
            reference = "tu"),
          icon = None,
          description = "desc")),
      status = status)
  }

  def createSolutionInfoContent = {
    QuestSolutionInfoContent(
      createContentReferenceStub,
      None)
  }

  def createSolutionStub(
    id: String = "sol id",
    cultureId: String = "cultureId",
    userId: String = "uid",
    questId: String = "qid",
    status: QuestSolutionStatus.Value = QuestSolutionStatus.OnVoting,
    level: Int = 1,
    themeId: String = "tid",
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
        themeId = themeId,
        questId = questId),
      status = status,
      rating = QuestSolutionRating(
        pointsRandom = points),
      voteEndDate = voteEndDate,
      lastModDate = lastModDate)
  }

  def createUserStub(
    id: String = "uid",
    cultureId: String = "cultureId",
    vip: Boolean = false,
    likedQuestProposalIds: List[List[String]] = List(),
    friends: List[Friendship] = List(),
    assets: Assets = Assets(1000000, 1000000, 1000000),
    mustVoteSolutions: List[String] = List(),
    favThemes: List[String] = List(),
    level: Int = 18,
    questProposalCooldown: Date = new Date(Long.MaxValue),
    takenTheme: Option[ThemeInfoWithID] = Some(ThemeInfoWithID("theme_id", createThemeStub().info))) = {

    User(
      id = id,
      demo = UserDemographics(
        cultureId = Some(cultureId)),
      history = UserHistory(
        likedQuestProposalIds = likedQuestProposalIds,
        selectedThemeIds = favThemes),
      privateDailyResults = List(DailyResult(
        startOfPeriod = new Date(),
        dailyAssetsDecrease = Assets())),
      profile = Profile(
        assets = assets,
        ratingToNextLevel = 100000,
        questSolutionContext = QuestSolutionContext(
          takenQuest = Some(QuestInfoWithID(
            "quest_id",
            QuestInfo(
              authorId = "author_id",
              themeId = "theme_id",
              vip = false,
              content = QuestInfoContent(ContentReference(ContentType.Photo, "", ""), None, "")))),
          questDeadline = new Date(Long.MaxValue)),
        questProposalContext = QuestProposalConext(
          approveReward = Assets(1, 2, 3),
          takenTheme = takenTheme,
          questProposalCooldown = questProposalCooldown),
        publicProfile = PublicProfile(
          vip = vip,
          level = level,
          bio = Bio(
            gender = Gender.Male)),
        rights = Rights.full),
      friends = friends,
      mustVoteSolutions = mustVoteSolutions)
  }

}