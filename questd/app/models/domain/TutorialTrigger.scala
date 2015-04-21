package models.domain

/**
 * Types of tutorial triggers.
 */
object TutorialTriggerType extends Enumeration {
  val Any = Value
  val TutorialElementClosed = Value
  val TutorialTaskCompleted = Value
  val LevelGained = Value
}

/**
 * Describes an action client application should take during the tutorial.
 */
case class TutorialTrigger (
  `type`: TutorialTriggerType.Value
  )

