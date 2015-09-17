package models.domain.user.timeline

import java.util.Date

import models.domain.base.ID


/**
 * A single entry from time line.
 *
 * @param id Id of time line entry.
 * @param reason Reason why entry was created.
 * @param actorId Id of actor action what created the entry.
 * @param objectType Type of object entry describes.
 * @param objectId Id of object entry describes.
 * @param entryDate Date when entry was added to the timeline.
 */
case class TimeLineEntry  (
  id: String = ID.generate,
  reason: TimeLineReason.Value,
  actorId: String,
  objectType: TimeLineType.Value,
  objectId: String,
  entryDate: Date = new Date())

