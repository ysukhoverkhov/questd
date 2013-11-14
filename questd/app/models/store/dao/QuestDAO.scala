package models.store.dao

import models.domain._

trait QuestDAO {

  def createQuest(o: Quest): Unit
  def readQuestByID(key: QuestID): Option[Quest]
  def updateQuest(o: Quest): Unit
  def deleteQuest(key: QuestID): Unit
  def allQuests: Iterator[Quest]

}

