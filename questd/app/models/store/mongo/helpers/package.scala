package models.store.mongo

import com.mongodb.MongoException
import models.store.exceptions.DatabaseException

package object helpers {

  private[mongo] def unlift(o: Option[String]): String = unlift(o, "")

  private[mongo] def unlift[T](o: Option[T], default: T): T = {
    o match {
      case Some(v) => v
      case _ => default
    }
  }

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

