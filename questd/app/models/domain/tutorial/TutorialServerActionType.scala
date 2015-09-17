package models.domain.tutorial

/**
 * Types of tutorial actions.
 */
object TutorialServerActionType extends Enumeration {
  val Dummy = Value
  val RemoveDailyTasksSuppression = Value
  val AssignDailyTasks = Value
}
