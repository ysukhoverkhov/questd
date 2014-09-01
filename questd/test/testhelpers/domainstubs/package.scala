package testhelpers

import java.util.Date

import models.domain._
import models.domain.base.ID
import models.domain.view.ThemeWithID


package object domainstubs {


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

  def createContentReferenceStub = ContentReference(
    contentType = ContentType.Photo,
    storage = "",
    reference = "")


  def createQuestStub(
    id: String,
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

  def createUserStub(
    cultureId: String = "cultureId",
    vip: Boolean = false,
    likedQuestProposalIds: List[List[String]] = List()) = {

    User(
      id = "user_id",
      demo = UserDemographics(
        cultureId = Some(cultureId)),
      history = UserHistory(likedQuestProposalIds = likedQuestProposalIds),
      profile = Profile(
        questProposalContext = QuestProposalConext(
          approveReward = Assets(1, 2, 3),
          takenTheme = Some(ThemeWithID("theme_id", createThemeStub().info)),
          questProposalCooldown = new Date(Long.MaxValue)),
        publicProfile = PublicProfile(vip = vip),
        rights = Rights.full))
  }

}
