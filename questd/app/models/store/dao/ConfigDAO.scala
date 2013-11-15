package models.store.dao

import models.domain.admin._

trait ConfigDAO extends BaseDAO[ConfigSection] {

  def readConfig: Configuration
}

