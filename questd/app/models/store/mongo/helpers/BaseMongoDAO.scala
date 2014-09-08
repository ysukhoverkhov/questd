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

/******************************
 * This should be a part of salat.
 */

trait QDAOMethods [ObjectType <: AnyRef, ID <: Any] {
  /** Returns a single object from this collection.
   *  @param t object for which to search
   *  @tparam A type view bound to DBObject
   *  @return (Option[ObjectType]) Some() of the object found, or <code>None</code> if no such object exists
   */
  def findAndModify(q: DBObject, o: DBObject, upsert: Boolean, returnNew: Boolean): Option[ObjectType]

}

class QSalatDAO[ObjectType <: AnyRef, ID <: Any](collection: MongoCollection)(implicit mot: Manifest[ObjectType],
                                                                                          mid: Manifest[ID], ctx: Context)
    extends SalatDAO[ObjectType, ID](collection) with QDAOMethods[ObjectType, ID]
{
  def findAndModify(q: DBObject, u: DBObject, upsert: Boolean, returnNew: Boolean): Option[ObjectType] = {
    collection.findAndModify(decorateQuery(q), MongoDBObject(), MongoDBObject(), false, u, returnNew, upsert).map(_grater.asObject(_))
  }

}

/*************************
 * End of "Should"
 *************************/



abstract class BaseMongoDAO[T <: ID: Manifest](collectionName: String)
  extends ModelCompanion[T, ObjectId] {

  val dao = new QSalatDAO[T, ObjectId](collection = mongoCollection(collectionName))
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
  }

  /**
   * Searches for object by query object.
   */
  def readById(key: String): Option[T] = wrapMongoException {
    findOne(makeKeyDbObject(key))
  }

  /**
   * Searches for object by query object.
   */
  def readByExample[R](fieldName: String, key: String)(implicit view: T => R): Option[R] = wrapMongoException {
    findOne(makeKeyDbObject(fieldName, key)).map(view(_))
  }

  /**
   * All objects with filter.
   */
  def findByExample(example: DBObject, sort: DBObject = MongoDBObject.empty, skip: Int = 0): Iterator[T] = wrapMongoException {
    find(example).sort(sort).skip(skip)
  }

  /**
   * Count or records. 
   */
  def countByExample(example: DBObject): Long = wrapMongoException {
    count(example)
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
  }

  /**
   * Searches for a object, modifies it and returns after modification
   */
  def findAndModify(key: String, u: DBObject): Option[T] = wrapMongoException {
    findAndModify(makeKeyDbObject(key), u)
  }

  /**
   * Searches for a object, modifies it and returns after modification
   */
  def findAndModify(f: DBObject, u: DBObject): Option[T] = wrapMongoException {
    dao.findAndModify(f, u, false, true)
  }
  
  /**
   * Delete object
   */
  def delete(key: String): Unit = wrapMongoException {
    val wr = remove(makeKeyDbObject(key))
  }

  /**
   * Clear all objects
   */
  def clear: Unit = wrapMongoException {
    val wr = remove(MongoDBObject())
  }
  
  /**
   * All objects
   */
  def all: Iterator[T] = wrapMongoException {
    find(MongoDBObject())
  }
  
}
