package models.domain

case class QuestInfoContent(
  media: ContentReference,
  icon: Option[ContentReference],
  description: String)

case class QuestInfo(
  authorId: String,
  content: QuestInfoContent,
  level: Int,
  vip: Boolean) {

}

