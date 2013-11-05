package models.store.dao

import models.domain.config._

private[store] trait ConfigDAO {

  def upsertSection(o: ConfigSection): Unit
  def readConfig: Configuration

}

