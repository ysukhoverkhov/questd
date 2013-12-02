package models.domain

object QuestDifficulty extends Enumeration {
  val Easy, Normal, Hard, Extreme = Value
}

object QuestDuration extends Enumeration {
  val Minutes, Hour, Day, TwoDays, Week = Value
}

// TODO make quest duration not integer in days but enum (after implementing voting).

case class QuestInfo(
    content: ContentReference,
    duration: Int = 1,
    // TODO: rename me to duration.
    durationNew: String = QuestDuration.Minutes.toString,
    difficulty: String = QuestDifficulty.Easy.toString)
    
    
