package models.domain.user.timeline

import models.domain.base.ID


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
  objectId: String)

