package models.domain.tutorial

import models.domain.base.ID

/**
 * A tutorial for a platform.
 */
case class Tutorial (
  id: String,
  elements: List[TutorialElement]
  ) extends ID
