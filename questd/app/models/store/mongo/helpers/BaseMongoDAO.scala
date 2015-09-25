package models.store.mongo.helpers

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import com.novus.salat.dao._
import models.domain.base.ID
import models.store.mongo.SalatContext._
import org.bson.types.ObjectId
import play.api.Play.current
import se.radley.plugin.salat._

/** ****************************
  * This should be a part of salat.
  */

trait QDAOMethods[ObjectType <: AnyRef, ID <: Any] {
  /** Returns a single object from this collection.
    * @param q object for which to search
    * @return (Option[ObjectType]) Some() of the object found, or <code>None</code> if no such object exists
    */
  def findAndModify(q: DBObject, o: DBObject, upsert: Boolean, returnNew: Boolean): Option[ObjectType]

}

class QSalatDAO[ObjectType <: AnyRef, ID <: Any](collection: MongoCollection)(implicit mot: Manifest[ObjectType],
  mid: Manifest[ID], ctx: Context)
  extends SalatDAO[ObjectType, ID](collection) with QDAOMethods[ObjectType, ID] {
  def findAndModify(q: DBObject, u: DBObject, upsert: Boolean, returnNew: Boolean): Option[ObjectType] = {
    collection.findAndModify(
      decorateQuery(q),
      MongoDBObject(),
      MongoDBObject(),
      remove = false,
      u,
      returnNew = returnNew,
      upsert = upsert).map(_grater.asObject(_))
  }
}

/** ***********************
  * End of "Should"
  * ************************/


abstract class BaseMongoDAO[T <: ID : Manifest](collectionName: String)
  extends ModelCompanion[T, ObjectId] {

  val dao = new QSalatDAO[T, ObjectId](collection = mongoCollection(collectionName))
  private val keyFieldName: String = "id"

  /**
   * Create
   */
  def create(o: T): Unit = wrapMongoException {
    save(o)
  }

  /**
   * Searches for object by query object.
   */
  def readById(key: String): Option[T] = wrapMongoException {
    findOne(makeKeyDbObject(key))
  }

  /**
   * Read several documents by their ids.
   * @param ids Ids of documents to read.
   * @param skip Number of documents to skip. 0 - do not skip.
   * @return Iterator with found documents.
   */
  def readManyByIds(ids: List[String], skip: Int = 0): Iterator[T] = wrapMongoException {
    find(MongoDBObject(
      keyFieldName -> MongoDBObject(
        "$in" -> ids))).skip(skip)
  }

  /**
   * Searches for object by query field.
   */
  def readByExample[R](fieldName: String, key: String)(implicit view: T => R): Option[R] = wrapMongoException {
    findOne(makeKeyDbObject(fieldName, key)).map(view)
  }

  /**
   * Searches for object by query object.
   */
  def readByExample[R](example: DBObject)(implicit view: T => R): Option[R] = wrapMongoException {
    findOne(example).map(view)
  }

  private def makeKeyDbObject(fieldName: String, key: String): DBObject = {
    makeKeyDbObject(fieldName -> key)
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
  def update(query: DBObject, updateRules: DBObject, multi: Boolean): Unit =
    update(query, updateRules, upsert = false, multi = multi)

  /**
   * Update object with new object
   */
  def update(key: String, u: T): Unit = updateInt(key, u, upsert = false, multi = false)

  /**
   * Update object with new object
   */
  def update(u: T): Unit = updateInt(u.id, u, upsert = false)

  /**
   * Update object with new object
   */
  def upsert(key: String, u: T): Unit = updateInt(key, u, upsert = true)

  /**
   * Update object with new object
   */
  def upsert(u: T): Unit = updateInt(u.id, u, upsert = true)

  private def updateInt(key: String, u: T, upsert: Boolean, multi: Boolean = false): Unit = wrapMongoException {
    update(makeKeyDbObject(key), toDBObject(u), upsert, multi = multi)
  }

  private def makeKeyDbObject(key: String): DBObject = {
    makeKeyDbObject(keyFieldName -> key)
  }

  private def makeKeyDbObject(example: (String, Any)*): DBObject = {
    MongoDBObject(example: _*)
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
    dao.findAndModify(f, u, upsert = false, returnNew = true)
  }

  /**
   * Delete object
   */
  def delete(key: String): Unit = wrapMongoException {
    remove(makeKeyDbObject(key))
  }

  /**
   * Clear all objects
   */
  def clear(): Unit = wrapMongoException {
    remove(MongoDBObject())
  }

  /**
   * All objects
   */
  def all: Iterator[T] = wrapMongoException {
    find(MongoDBObject())
  }

}
