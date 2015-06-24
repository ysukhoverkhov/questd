package models.domain.tutorial

/**
 * Types of tutorial actions.
 */
object TutorialActionType extends Enumeration {
  val AssignTask = Value
  val AssignQuest = Value
  val IncTask = Value
  val Message = Value
  val FocusOnGUIElement = Value
  val RemoveFocus = Value
  val PlayAnimation = Value
  val StopAnimation = Value
}
