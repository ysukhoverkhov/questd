package models.store.dao.user

import java.util.Date

import models.domain.user.User
import models.domain.user.timeline.{TimeLineEntry, TimeLineReason}

/**
 * DAO for timeline things in user.
 */
trait UserTimeLineDAO {

  /**
   * Set new time to populate time line at.
   * @param id id of a user to set time.
   * @param time New time.
   * @return Updated user.
   */
  def setTimeLinePopulationTime(id: String, time: Date): Option[User]

  /**
   * Adds one entry to time line.
   * @param id Id of a user to add to.
   * @param entry Entry to add.
   * @return user after modifications.
   */
  def addEntryToTimeLine(id: String, entry: TimeLineEntry): Option[User]

  /**
   * Adds single time line entry to several users.
   * @param ids Ids of users to add to.
   * @param entry Entry to add.
   */
  def addEntryToTimeLineMulti(ids: List[String], entry: TimeLineEntry): Unit

  /**
   * Removes entry from time line.
   * @param id Id of a user to add to.
   * @param objectId Object to remove.
   * @return user after modifications.
   */
  def removeEntryFromTimeLineByObjectId(id: String, objectId: String): Option[User]

  /**
   * Updates status of timeline entry.
   *
   * @param id Id of user to update timeline entry at.
   * @param entryId Id of timeline entry to update.
   * @param reason New reason of entry.
   * @return Updated user.
   */
  def updateTimeLineEntry(id: String, entryId: String, reason: TimeLineReason.Value): Option[User]
}
