package models.domain

object QuestDifficulty extends Enumeration {
  val Easy, Normal, Hard, Extreme = Value
}

object QuestDuration extends Enumeration {
  val Minutes, Hours, Day, TwoDays, Week = Value
}

case class QuestInfoContent(
  media: ContentReference,
  icon: Option[ContentReference],
  description: String)

case class QuestInfo(
  content: QuestInfoContent,
  level: Int = 0,
  duration: String = QuestDuration.Minutes.toString,
  difficulty: String = QuestDifficulty.Easy.toString) {

  def daysDuration = QuestDuration.withName(duration) match {
    case QuestDuration.Minutes => 1
    case QuestDuration.Hours => 1
    case QuestDuration.Day => 1
    case QuestDuration.TwoDays => 2
    case QuestDuration.Week => 7
  }
}
    
