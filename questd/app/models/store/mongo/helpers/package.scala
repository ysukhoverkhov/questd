package models.store.mongo

import com.mongodb.MongoException
import models.store.exceptions.DatabaseException

package object helpers {

  import scala.language.implicitConversions

  implicit def listToSuperFlattenList[T](_l: List[List[T]]) = {
    SuperFlattenList(_l)
  }

  object SuperFlattenList {
    def apply[T](_l: List[List[T]]) = new SuperFlattenList(_l)
  }
  class SuperFlattenList[T](val _l: List[List[T]]) {
    def mongoFlatten: List[T] = {
      import com.mongodb._

      if (_l.size > 0 && _l.head.getClass() == classOf[BasicDBList]) {
        val rv = for (
          out <- _l.asInstanceOf[List[BasicDBList]];
          in <- out.toArray().asInstanceOf[Array[Object]]
        ) yield {
          in
        }
        rv.asInstanceOf[List[T]]
      } else {
        _l.flatten
      }
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

