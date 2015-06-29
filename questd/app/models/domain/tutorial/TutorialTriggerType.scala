package models.domain.tutorial

/**
 * Types of tutorial triggers.
 */
object TutorialTriggerType extends Enumeration {
  val Any = Value
  val TutorialElementClosed = Value
  val TutorialTaskCompleted = Value
  val ScreenOpened = Value
  val ModalScreenOpened = Value
  val ModalScreenDismissed = Value
  val ButtonPressed = Value
  val LevelGained = Value
  val TasksPanelMaximized = Value
  val TasksPanelCollapsedFromMaximized = Value
  val ContentLiked = Value
  val SidePanelOpened = Value
  val OurBattleIsShown = Value
}
