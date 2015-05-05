package models.domain

/**
 * Types of tutorial conditions.
 */
object TutorialConditionType extends Enumeration {
  val TutorialElementClosed = Value
  val ProfileVariableState = Value
  val ScreenOpened = Value
}

/**
 * Describes an action client application should take during the tutorial.
 */
case class TutorialCondition (
  conditionType: TutorialConditionType.Value,
  params: Map[String, String] = Map.empty
  )

