package models.store.mongo.dao

import scala.language.postfixOps
import scala.language.implicitConversions
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
import models.domain.config._

/**
 * This class is representing user form the database.
 */
private[mongo] case class ConfigSectionDB(
  name: Option[String],
  values: Option[Map[String, String]] = None)

/**
 * DOA for Config objects
 */
private[mongo] class MongoConfigDAO
  extends ConfigDAO
  with ModelCompanion[ConfigSectionDB, ObjectId]
  with BaseMongoDAO[ConfigSectionDB] {

  val dao = new SalatDAO[ConfigSectionDB, ObjectId](collection = mongoCollection("configs")) {}

  /**
   * Conversion from domain object  to db object
   */
  protected implicit def domainToDB(dom: ConfigSection): ConfigSectionDB = {
    ConfigSectionDB(Some(dom.name), Some(dom.values))
  }

  /**
   * Conversion from db object to domain object
   */
  protected implicit def dbToDomain(db: ConfigSectionDB): ConfigSection = {
    ConfigSection(
      unlift(db.name),
      unlift(db.values, Map()))
  }

  def upsertSection(o: ConfigSection): Unit = upsert(ConfigSectionDB(Some(o.name)), o)

  def readConfig: Configuration = {
    val a: List[ConfigSection] = all map (o => dbToDomain(o))

    Configuration(
      a.foldLeft[Map[String, ConfigSection]](Map()) { (m, v) => m + (v.name -> v) })
  }

}

