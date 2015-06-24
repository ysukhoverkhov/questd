package models.domain.tutorial

/**
 * Describes an action client application should take during the tutorial.
 */
case class TutorialCondition (
  conditionType: TutorialConditionType.Value,
  params: Map[String, String] = Map.empty
  )
