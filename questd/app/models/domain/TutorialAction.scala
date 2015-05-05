package models.domain

/**
 * Types of tutorial actions.
 */
object TutorialActionType extends Enumeration {
  val AssignTask = Value
  val IncTask = Value
  val Message = Value
  val FocusOnGUIElement = Value
  val RemoveFocus = Value
  val PlayAnimation = Value
  val StopAnimation = Value
}

/**
 * Describes an action client application should take during the tutorial.
 */
case class TutorialAction (
  actionType: TutorialActionType.Value,
  params: Map[String, String] = Map.empty
  )

