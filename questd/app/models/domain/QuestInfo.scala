package models.domain

object QuestDifficulty extends Enumeration {
  val Easy, Normal, Hard, Extreme = Value
}

object QuestDuration extends Enumeration {
  val Minutes, Hour, Day, Week = Value
}

case class QuestInfoContent(
  media: ContentReference,
  icon: Option[ContentReference],
  description: String)

case class QuestInfo(
  authorId: String,
  themeId: String,
  content: QuestInfoContent,
  level: Int = 0,
  duration: QuestDuration.Value = QuestDuration.Minutes,
  difficulty: QuestDifficulty.Value = QuestDifficulty.Easy,
  vip: Boolean) {

  def daysDuration = duration match {
    case QuestDuration.Minutes => 1
    case QuestDuration.Hour => 1
    case QuestDuration.Day => 1
    case QuestDuration.Week => 7
  }

  def minutesDuration = duration match {
    case QuestDuration.Minutes => 40
    case QuestDuration.Hour => 120
    case QuestDuration.Day => 60 * 24
    case QuestDuration.Week => 60 * 34 * 7
  }
}
    
