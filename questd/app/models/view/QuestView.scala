package models.view

import models.domain.common.ContentVote
import models.domain.quest.{Quest, QuestInfo, QuestRating}
import models.domain.user.User

case class QuestView (
  id: String,
  info: QuestInfo,
  rating: QuestRating,
  myVote: Option[ContentVote.Value],
  mySolutionId: Option[String])

/**
 * Companion.
 */
object QuestView {
  def make(q: Quest, u: User): QuestView = {
    QuestView(
      id = q.id,
      info = q.info,
      rating = q.rating,
      myVote = u.stats.votedQuests.get(q.id),
      mySolutionId = u.stats.solvedQuests.get(q.id))
  }
}
