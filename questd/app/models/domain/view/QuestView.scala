package models.domain.view

import models.domain._

case class QuestView (
    id: String,
    obj: QuestInfo,
    rating: Option[QuestRating] = None)
