package models.store.mongo.helpers

import play.Logger
import play.api.Play.current
import org.bson.types.ObjectId
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import se.radley.plugin.salat._
import com.mongodb._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import models.store.mongo.SalatContext._
import models.store.exceptions.DatabaseException
import models.domain.base.ID

abstract class BaseMongoDAO[T <: ID: Manifest](collectionName: String)
  extends ModelCompanion[T, ObjectId] {

  val dao = new SalatDAO[T, ObjectId](collection = mongoCollection(collectionName)) {}
  private val keyFieldName: String = "id"

  private def makeKeyDbObject(key: String): DBObject = {
    makeKeyDbObject(keyFieldName -> key)
  }

  private def makeKeyDbObject(fieldName: String, key: String): DBObject = {
    makeKeyDbObject(fieldName -> key)
  }

  private def makeKeyDbObject(example: (String, Any)*): DBObject = {
    MongoDBObject(example:_*)
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
  def readByID(key: String): Option[T] = wrapMongoException {
    findOne(makeKeyDbObject(key)) match {
      case None => None
      case Some(r) => Some(r)
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
  def update(u: T): Unit = updateInt(u.id, u, false)

  /**
   * Update object with new object
   */
  def upsert(key: String, u: T): Unit = updateInt(key, u, true)

  /**
   * Update object with new object
   */
  def upsert(u: T): Unit = updateInt(u.id, u, true)

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
  
  /**
   * All objects with filter.
   */
  def allByExample(example: (String, Any)*): Iterator[T] = wrapMongoException {
    find(makeKeyDbObject(example:_*))
  } 

}
