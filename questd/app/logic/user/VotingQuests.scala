package logic.user

import logic._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._

/**
 * All logic about voting quests is here.
 */
trait VotingQuests { this: UserLogic =>

  /**
   * Check is our user can vote for given quest with given vote.
   */
  def canVoteQuest(questId: String, vote: ContentVote.Value) = {
    val questFromTimeLine = user.timeLine.find(_.objectId == questId)

    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuests))
      NotEnoughRights
    else if (questFromTimeLine == None)
      OutOfContent
    else if (questFromTimeLine.get.ourVote != None)
      InvalidState
    else if (user.demo.cultureId == None || user.profile.publicProfile.bio.gender == Gender.Unknown)
      IncompleteBio
    else
      OK
  }

  /**
   * @return None if no more quests to vote for today.
   */
  // TODO: move me to time line logic
  def getRandomQuestForTimeLine: Option[Quest] = {
    getRandomQuest
  }

}

