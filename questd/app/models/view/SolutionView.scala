package models.view

import models.domain.common.ContentVote
import models.domain.solution.{Solution, SolutionInfo, SolutionRating}
import models.domain.user.User

case class SolutionView (
  id: String,
  info: SolutionInfo,
  rating: Option[SolutionRating],
  myVote: Option[ContentVote.Value])


/**
 * Companion.
 */
object SolutionView {
  def make(s: Solution, u: User): SolutionView = {
    SolutionView(
      id = s.id,
      info = s.info,
      rating = Some(s.rating),
      myVote = u.stats.votedSolutions.get(s.id))
  }
}

