package models.domain.tutorial

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
