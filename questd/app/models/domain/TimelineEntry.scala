package models.domain


import models.domain.base.ID

/**
 * Reason why the entry is in time line.
 */
object TimeLineReason extends Enumeration {

  /**
   * Someone created something and it go to time line.
   */
  val Created = Value

  /**
   * Someone liked something and it got to time line.
   */
  val Liked = Value

  /**
   * Someone has the thing and we are telling about it now. This means the thing is put in time line on at the time of
   * creation but al later random time.
   */
  val Has = Value
}

/**
 * Type of time line entry.
 */
object TimeLineType extends Enumeration {

  val Quest = Value
  val Solution = Value
  val Battle = Value
}

/**
 * A single entry from time line.
 *
 * @param id Id of time line entry.
 * @param reason Reason why entry was created.
 * @param actorId Id of actor action what created the entry.
 * @param objectType Type of object entry describes.
 * @param objectId Id of object entry describes.
 */
case class TimeLineEntry  (
  id: String = ID.generateUUID(),
  reason: TimeLineReason.Value,
  actorId: String,
  objectType: TimeLineType.Value,
  objectId: String,
  ourVote: Option[ContentVote.Value] = None)

