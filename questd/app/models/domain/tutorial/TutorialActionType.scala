package models.domain.tutorial

/**
 * Types of tutorial actions.
 */
object TutorialActionType extends Enumeration {
  val Dummy = Value
  val CloseTutorialElement = Value
  val AssignTutorialTask = Value
  val AssignTutorialQuest = Value
  val IncTutorialTask = Value
  val Message = Value
  val FocusOnGUIElement = Value
  val RemoveFocus = Value
  val PlayAnimation = Value
  val StopAnimation = Value
  val ScrollToNearestBattleInTimeLine = Value
  val CreateTutorialBattles = Value
  val SetReminder = Value
}
