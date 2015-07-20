package models.domain.tutorial

/**
 * Types of tutorial conditions.
 */
object TutorialConditionType extends Enumeration {
  val Dummy = Value
  val TutorialElementClosed = Value
  val TutorialTaskActive = Value
  val TutorialTaskCompleted = Value
  val ProfileVariableState = Value
  val ScreenOpened = Value
  val ModalScreenOpened = Value
}
