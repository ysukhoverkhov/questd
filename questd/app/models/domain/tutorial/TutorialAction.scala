package models.domain.tutorial

/**
 * Describes an action client application should take during the tutorial.
 */
case class TutorialAction (
  actionType: TutorialActionType.Value,
  params: Map[String, String] = Map.empty
  )
