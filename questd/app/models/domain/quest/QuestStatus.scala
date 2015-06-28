package models.domain.quest

/**
 * Status of quest
 */
object QuestStatus extends Enumeration {
  val InRotation, CheatingBanned, IACBanned, OldBanned, AdminBanned, ForTutorial = Value
}
