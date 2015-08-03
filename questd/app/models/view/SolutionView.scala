package models.view

import models.domain.common.ContentVote
import models.domain.solution.{SolutionInfo, SolutionRating}

case class SolutionView (
    id: String,
    info: SolutionInfo,
    rating: Option[SolutionRating],
    myVote: Option[ContentVote.Value])
