package models.domain.view

import models.domain._

case class QuestView (
    id: String,
    info: QuestInfo,
    rating: Option[QuestRating],
    myVote: Option[ContentVote.Value])
