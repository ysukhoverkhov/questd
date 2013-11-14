package models.store.dao

import models.domain.admin._

private[store] trait ConfigDAO extends BaseDAO[ConfigSection] {

  def readConfig: Configuration
}

