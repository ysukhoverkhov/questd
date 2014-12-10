package logic.user

import java.util.Date
import logic._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import models.domain.ContentType._

/**
 * All logic related to proposing quests.
 */
trait CreatingQuests { this: UserLogic =>

  /**
   * Is user potentially eligible for proposing quest today.
   */
  def canProposeQuestToday = {
    user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoQuests) &&
    user.profile.questCreationContext.questCreationCoolDown.before(new Date())
  }

  /**
   * Is user can propose quest of given type.
   */
  def canCreateQuest(questContent: QuestInfoContent) = {
    val content = questContent.media.contentType match {
      case Photo => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoQuests)
      case Video => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitVideoQuests)
    }

    if (!content)
      NotEnoughRights
    else if (!canProposeQuestToday)
      CoolDown
    else if (questContent.description.length > api.config(api.ConfigParams.ProposalMaxDescriptionLength).toInt)
      LimitExceeded
    else if (user.demo.cultureId == None || user.profile.publicProfile.bio.gender == Gender.Unknown)
      IncompleteBio
    else
      OK
  }

  /**
   *
   */
  def getCoolDownForQuestCreation: Date = {
    import com.github.nscala_time.time.Imports._
    import org.joda.time.DateTime

    val daysToSkip = questProposalPeriod(user.profile.publicProfile.level)

    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    (DateTime.now(tz) + daysToSkip.days).hour(constants.FlipHour).minute(0).second(0) toDate ()
  }

  // TODO: invent me and write in desdoc as well.
  def penaltyForCheatingQuest = {
    Assets()
    //(rewardForMakingApprovedQuest * QuestProposalCheatingPenalty) clampTop user.profile.assets
  }

  // TODO: invent me and write in desdoc as well.
  def penaltyForIACQuest = {
    Assets()
//    (rewardForMakingApprovedQuest * QuestProposalIACPenalty) clampTop user.profile.assets
  }

  /**
   * How much it'll be for a single friend to help us with proposal.
   */
  def costOfAskingForHelpWithProposal = {
    Assets(coins = coinsToInviteFriendForVoteQuestProposal(user.profile.publicProfile.level))
  }

}
