package models.domain.tutorial

/**
 * Types of tutorial conditions.
 */
object TutorialConditionType extends Enumeration {
  val TutorialElementClosed = Value
  val TutorialTaskActive = Value
  val ProfileVariableState = Value
  val ScreenOpened = Value
}
