package models.store.mongo.helpers

import com.novus.salat.dao.ModelCompanion
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import models.store.exceptions.DatabaseException
import play.Logger
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

trait BaseMongoDAO[T <: AnyRef, K <: AnyRef] { this: ModelCompanion[T, ObjectId] =>

  protected def keyFieldName: String

  private def makeKeyDbObject(key: K): DBObject = {
    MongoDBObject(keyFieldName -> key)
  }

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
  def read[R](key: K)(implicit view: T => R): Option[R] = wrapMongoException {
    findOne(makeKeyDbObject(key)) match {
      case None => None
      case Some(r) => Some(view(r))
    }
  }

  /**
   * Searches for object by query object.
   */
  def readByExample[R](q: T)(implicit view: T => R): Option[R] = wrapMongoException {
    findOne(toDBObject(q)) match {
      case None => None
      case Some(r) => Some(view(r))
    }
  }

  /**
   * Update object with new object
   */
  def update(key: K, u: T): Unit = updateInt(key, u, false)

  /**
   * Update object with new object
   */
  def upsert(key: K, u: T): Unit = updateInt(key, u, true)

  private def updateInt(key: K, u: T, upsert: Boolean): Unit = wrapMongoException {
    val wr = update(makeKeyDbObject(key), toDBObject(u), upsert, false)

    if (!wr.getLastError().ok) {
      throw new DatabaseException(wr.getLastError().getErrorMessage())
    }
  }

  /**
   * Delete object
   */
  def delete(key: K): Unit = wrapMongoException {
    val wr = remove(makeKeyDbObject(key))

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
