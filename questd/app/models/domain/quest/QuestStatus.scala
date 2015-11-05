package models.domain.quest

/**
 * Status of quest
 */
object QuestStatus extends Enumeration {
  val InRotation, ForTutorial, CheatingBanned, IACBanned, OldBanned, AdminBanned, AuthorBanned = Value
}
