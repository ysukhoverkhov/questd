package models.store.dao

import models.domain.admin._

private[store] trait ConfigDAO {

  def upsertSection(o: ConfigSection): Unit
  def deleteSection(name: String): Unit
  def readConfig: Configuration

}

