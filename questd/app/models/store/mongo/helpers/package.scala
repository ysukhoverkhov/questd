package models.store.mongo

import com.mongodb.MongoException
import models.store.exceptions.DatabaseException

package object helpers {

  /**
   * Wrapper for handling unknown exceptions.
   */
  private[mongo] def wrapMongoException[T](f: => T): T = try {
    f
  } catch {
    case ex: MongoException => {
      throw new DatabaseException(ex)
    }
  }

}

