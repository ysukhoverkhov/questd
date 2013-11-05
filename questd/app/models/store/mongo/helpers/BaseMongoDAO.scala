package models.store.mongo.helpers

import com.novus.salat.dao.ModelCompanion
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._

import models.store.exceptions.DatabaseException

trait BaseMongoDAO[T <: AnyRef] { this: ModelCompanion[T, ObjectId] =>
  
  /**
   * Create
   */
  def create(o: T): Unit = wrapMongoException {
    val wr = save(o)
    
    if (!wr.getLastError().ok) {
      throw new DatabaseException(wr.getLastError().getErrorMessage())
    }
  }

  /**
   * Searches for object by query object.
   */
  def read[R](o: T)(implicit view: T => R): Option[R] = wrapMongoException {
    findOne(toDBObject(o)) match {
      case None => None
      case Some(r) => Some(view(r))
    }
  }

  /**
   * Update object with new object
   */
  def update(q: T, u: T): Unit = updateInt(q, u, false)

  /**
   * Update object with new object
   */
  def upsert(q: T, u: T): Unit = updateInt(q, u, true)
  
  private def updateInt(q: T, u: T, upsert: Boolean): Unit = wrapMongoException {
    val wr = update(toDBObject(q), toDBObject(u), upsert, false)

    if (!wr.getLastError().ok) {
      throw new DatabaseException(wr.getLastError().getErrorMessage())
    }
  }

  /**
   * Delete object
   */
  def delete(o: T): Unit = wrapMongoException {
    val wr = remove(o)

    if (!wr.getLastError().ok) {
      throw new DatabaseException(wr.getLastError().getErrorMessage())
    }
  }

  /**
   * All objects
   */
  def all: List[T] = wrapMongoException {
    // TODO OPTIMIZATION this will be very slow and will fetch everything.
    List() ++ find(MongoDBObject())
  }

}
