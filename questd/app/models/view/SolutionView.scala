package models.view

import models.domain.common.ContentVote
import models.domain.solution.{Solution, SolutionInfo, SolutionRating}
import models.domain.user.User

case class SolutionView (
  id: String,
  info: SolutionInfo,
  rating: SolutionRating,
  myVote: Option[ContentVote.Value],
  mySolutionIdForSameQuest: Option[String],
  battleIdCompetingWithUs: Option[String])


/**
 * Companion.
 */
object SolutionView {
  def apply(s: Solution, u: User): SolutionView = {
    SolutionView(
      id = s.id,
      info = s.info,
      rating = s.rating,
      myVote = u.stats.votedSolutions.get(s.id),
      mySolutionIdForSameQuest = u.stats.solvedQuests.get(s.info.questId),
      battleIdCompetingWithUs = u.stats.participatedBattles.find(e => e._2.solutionIds.contains(s.id)).map(_._1))
  }
}

