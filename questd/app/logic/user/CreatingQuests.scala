package logic.user

import java.util.Date

import controllers.domain.app.user.CreateQuestCode
import logic._
import logic.constants._
import logic.functions._
import models.domain.common.Assets
import models.domain.common.ContentType._
import models.domain.quest.QuestInfoContent
import models.domain.user.profile.Functionality

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
  def canCreateQuest(questContent: QuestInfoContent): CreateQuestCode.Value = {
    import CreateQuestCode._

    val content = questContent.media.contentType match {
      case Photo => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoQuests)
      case Video => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitVideoQuests)
    }

    if (!content)
      NotEnoughRights
    else if (!canProposeQuestToday)
      QuestCreationCoolDown
    else if (questContent.description.length > api.config(api.DefaultConfigParams.QuestMaxDescriptionLength).toInt)
      DescriptionLengthLimitExceeded
    else if (!bioComplete)
      IncompleteBio
    else
      OK
  }

  /**
   * Calculates what level should hve quest created by user.
   */
  def calculateQuestLevel: Int = {
    val k = MaxLevel / (MaxLevel - levelFor(Functionality.SubmitPhotoQuests) + 1).toDouble
    val fractionalLevel: Double = (user.profile.publicProfile.level - levelFor(Functionality.SubmitPhotoQuests) + 1) * k

    if (rand.nextDouble() > 0.5)
      fractionalLevel.floor.toInt
    else
      fractionalLevel.ceil.toInt
  }

  /**
   * When we will be able to propose new quest.
   */
  def getCoolDownForQuestCreation: Date = {
    import com.github.nscala_time.time.Imports._
    import org.joda.time.DateTime

    val daysToSkip = questCreationPeriod(user.profile.publicProfile.level)

    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    (DateTime.now(tz) + daysToSkip.days).hour(constants.FlipHour).minute(0).second(0).toDate
  }

  def penaltyForCheatingQuest = {
    Assets(rating = user.profile.assets.rating / 4)
  }

  def penaltyForIACQuest = {
    Assets(rating = user.profile.assets.rating / 2)
  }

  /**
   * How much it'll be for a single friend to help us with proposal.
   */
  def costOfAskingForHelpWithQuest = {
    Assets(coins = coinsToInviteFriendForVoteQuest(user.profile.publicProfile.level))
  }

}
