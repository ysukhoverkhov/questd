package models.domain.view

import models.domain._

case class SolutionView (
    id: String,
    obj: SolutionInfo,
    rating: Option[SolutionRating] = None)
