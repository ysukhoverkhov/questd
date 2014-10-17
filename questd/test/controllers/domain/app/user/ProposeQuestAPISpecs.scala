package controllers.domain.app.user

import controllers.domain._
import models.domain._

class ProposeQuestAPISpecs extends BaseAPISpecs {


  def createQuest = {
    QuestInfoContent(ContentReference(ContentType.Photo, "", ""), None, "")
  }

  "Propose Quest API" should {

    // TODO: clean me up.
//    "Create regular quests for regular users" in context {
//
//      val u = createUserStub(vip = false)
//      val q = createQuest
//
//      user.resetQuestProposal(any, any) returns Some(u)
//      user.addEntryToTimeLine(any, any) returns Some(u)
//
//      val result = api.proposeQuest(ProposeQuestRequest(u, q))
//
//      result.body must beSome[ProposeQuestResult].which(r => r.allowed == ProfileModificationResult.OK)
//
//      there was one(user).addEntryToTimeLine(any, any)
//      there was one(user).addEntryToTimeLineMulti(any, any)
//      there was one(quest).create(
//        Quest(
//          id = anyString,
//          cultureId = "cultureId",
//          approveReward = u.profile.questProposalContext.approveReward,
//          info = QuestInfo(
//            authorId = u.id,
//            themeId = u.profile.questProposalContext.takenTheme.get.id,
//            content = q,
//            vip = false)))
//    }

//    "Create VIP quests for VIP users" in context {
//      val u = createUserStub(vip = true)
//      val q = createQuest
//
//      user.resetQuestProposal(any, any) returns Some(u)
//      user.addEntryToTimeLine(any, any) returns Some(u)
//
//      val result = api.proposeQuest(ProposeQuestRequest(u, q))
//
//      result.body.get.allowed must beEqualTo(ProfileModificationResult.OK)
//
//      there was one(user).addEntryToTimeLine(any, any)
//      there was one(user).addEntryToTimeLineMulti(any, any)
//      there was one(quest).create(
//        Quest(
//          id = anyString,
//          cultureId = "cultureId",
//          approveReward = u.profile.questProposalContext.approveReward,
//          info = QuestInfo(
//            authorId = u.id,
//            themeId = u.profile.questProposalContext.takenTheme.get.id,
//            content = q,
//            vip = true)))
//    }
  }
}

