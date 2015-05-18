package models.domain.quest

/**
 * Status of quest
 */
object QuestStatus extends Enumeration {
  val InRotation, CheatingBanned, IACBanned, OldBanned = Value
}
