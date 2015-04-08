package models.domain

import models.domain.base.ID

/**
 * Types of tutorial actions.
 */
object TutorialPlatform extends Enumeration {
  val iPhone = Value
}

/**
 * A tutorial for a platform.
 */
case class Tutorial (
  id: String,
  elements: List[TutorialElement]
  ) extends ID

