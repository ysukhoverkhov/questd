package models.view

import models.domain._
import models.domain.common.ContentVote
import models.domain.quest.{QuestRating, QuestInfo}

case class QuestView (
    id: String,
    info: QuestInfo,
    rating: Option[QuestRating],
    myVote: Option[ContentVote.Value])
