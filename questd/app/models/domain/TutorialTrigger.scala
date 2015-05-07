package models.domain

/**
 * Types of tutorial triggers.
 */
object TutorialTriggerType extends Enumeration {
  val Any = Value
  val TutorialElementClosed = Value
  val TutorialTaskCompleted = Value
  val ScreenOpened = Value
  val ButtonPressed = Value
  val LevelGained = Value
  val TasksPanelMaximized = Value
  val TasksPanelCollapsedFromMaximized = Value
}

/**
 * Describes an action client application should take during the tutorial.
 */
case class TutorialTrigger (
  triggerType: TutorialTriggerType.Value,
   params: Map[String, String] = Map.empty
  )

