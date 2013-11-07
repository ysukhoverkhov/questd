package models.store.mongo.dao

import scala.language.postfixOps
import scala.language.implicitConversions
import scala.language.reflectiveCalls
import play.Logger
import play.api.db._
import play.api.Play._
import se.radley.plugin.salat._
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.CommandResult
import com.mongodb.DBObject
import models.store.mongo.SalatContext._
import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._
import com.mongodb.casbah.commons.MongoDBObject

/**
 * This class is representing user form the database.
 */
private[mongo] case class ConfigSectionDB(
  name: Option[String],
  values: Map[String, String] = Map())

/**
 * DOA for Config objects
 */
private[mongo] class MongoConfigDAO
  extends ConfigDAO
  with ModelCompanion[ConfigSectionDB, ObjectId]
  with BaseMongoDAO[ConfigSectionDB, String] {

  val dao = new SalatDAO[ConfigSectionDB, ObjectId](collection = mongoCollection("configs")) {}
  final protected val keyFieldName = "name"

  /**
   * Conversion from domain object  to db object
   */
  protected implicit def domainToDB(dom: ConfigSection): ConfigSectionDB = {
    ConfigSectionDB(Some(dom.name), dom.values)
  }

  /**
   * Conversion from db object to domain object
   */
  protected implicit def dbToDomain(db: ConfigSectionDB): ConfigSection = {
    ConfigSection(
      unlift(db.name),
      db.values)
  }

  def upsertSection(o: ConfigSection): Unit = upsert(o.name, o)

  def deleteSection(name: String): Unit = delete(name)

  def readConfig: Configuration = {
    val a: List[ConfigSection] = all map (o => dbToDomain(o) )

    Configuration(
      a.foldLeft[Map[String, ConfigSection]](Map()) { (m, v) => m + (v.name -> v) })
  }

}

