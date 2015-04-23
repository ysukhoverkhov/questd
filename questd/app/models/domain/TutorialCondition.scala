package models.domain

/**
 * Types of tutorial conditions.
 */
object TutorialConditionType extends Enumeration {
  val TutorialElementClosed = Value
  val ProfileVariableState = Value
}

/**
 * Describes an action client application should take during the tutorial.
 */
case class TutorialCondition (
  `type`: TutorialConditionType.Value,
  params: Map[String, String] = Map.empty
  )

