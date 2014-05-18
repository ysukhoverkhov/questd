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
  themeId: String,
  content: QuestInfoContent,
  level: Int = 0,
  duration: String = QuestDuration.Minutes.toString,
  difficulty: String = QuestDifficulty.Easy.toString,
  vip: Boolean) {

  def daysDuration = QuestDuration.withName(duration) match {
    case QuestDuration.Minutes => 1
    case QuestDuration.Hours => 1
    case QuestDuration.Day => 1
    case QuestDuration.TwoDays => 2
    case QuestDuration.Week => 7
  }

  def minutesDuration = QuestDuration.withName(duration) match {
    case QuestDuration.Minutes => 20
    case QuestDuration.Hours => 120
    case QuestDuration.Day => 60 * 24
    case QuestDuration.TwoDays => 60 * 48
    case QuestDuration.Week => 60 * 34 * 7
  }
}
    
