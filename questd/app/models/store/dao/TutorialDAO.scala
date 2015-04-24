package models.store.dao

import models.domain._

trait TutorialDAO extends BaseDAO[Tutorial] {

  /**
   * Adds new element to tutorial script.
   *
   * @param id If of an tutorial to add element to.
   * @param tutorialElement Element to add
   * @return Updated tutorial.
   */
  def addElement(id: String, tutorialElement: TutorialElement): Option[Tutorial]

  /**
   * Removes element from tutorial.
   *
   * @param id Id of tutorial to remove element from.
   * @param tutorialElementId Id of element to remove.
   * @return Uprated tutorial.
   */
  def deleteElement(id: String, tutorialElementId: String): Option[Tutorial]
}

