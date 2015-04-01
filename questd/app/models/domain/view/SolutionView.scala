package models.domain.view

import models.domain._

case class SolutionView (
    id: String,
    info: SolutionInfo,
    rating: Option[SolutionRating] = None,
    myVote: Option[ContentVote.Value] = None)