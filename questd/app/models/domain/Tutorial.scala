package models.domain


/**
 * A tutorial for a platform.
 */
case class Tutorial (
  platform: String,
  elements: List[TutorialElement]
  )

