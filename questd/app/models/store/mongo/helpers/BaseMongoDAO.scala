package models.store.mongo.helpers

import com.novus.salat.dao.ModelCompanion
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import models.store.exceptions.DatabaseException
import play.Logger
import com.mongodb.casbah.commons.MongoDBObject
import org.bson.types.ObjectId

trait BaseMongoDAO[T <: AnyRef] { this: ModelCompanion[T, ObjectId] =>

  protected def keyFieldName: String

  private def makeKeyDbObject(key: String): DBObject = {
    MongoDBObject(keyFieldName -> key)
  }

  private def makeKeyDbObject(fieldName: String, key: String): DBObject = {
    MongoDBObject(fieldName -> key)
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
  def read[R](key: String)(implicit view: T => R): Option[R] = wrapMongoException {
    findOne(makeKeyDbObject(key)) match {
      case None => None
      case Some(r) => Some(view(r))
    }
  }

  /**
   * Searches for object by query object.
   */
  def readByExample[R](fieldName: String, key: String)(implicit view: T => R): Option[R] = wrapMongoException {
    findOne(makeKeyDbObject(fieldName, key)) match {
      case None => None
      case Some(r) => Some(view(r))
    }
  }

  /**
   * Update object with new object
   */
  def update(key: String, u: T): Unit = updateInt(key, u, false)

  /**
   * Update object with new object
   */
  def upsert(key: String, u: T): Unit = updateInt(key, u, true)

  private def updateInt(key: String, u: T, upsert: Boolean): Unit = wrapMongoException {
    val wr = update(makeKeyDbObject(key), toDBObject(u), upsert, false)

    if (!wr.getLastError().ok) {
      throw new DatabaseException(wr.getLastError().getErrorMessage())
    }
  }

  /**
   * Delete object
   */
  def delete(key: String): Unit = wrapMongoException {
    val wr = remove(makeKeyDbObject(key))

    if (!wr.getLastError().ok) {
      throw new DatabaseException(wr.getLastError().getErrorMessage())
    }
  }

  /**
   * All objects
   */
  def all: Iterator[T] = wrapMongoException {
    find(MongoDBObject())
  }

}
