package models.domain.user.timeline

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

  /**
   * Is the timeline element should be hidden.
   */
  val Hidden = Value
 }
