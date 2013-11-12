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
import models.domain.admin._
import com.mongodb.casbah.commons.MongoDBObject


/**
 * DOA for Config objects
 */
private[mongo] class MongoConfigDAO
  extends ConfigDAO
  with ModelCompanion[ConfigSection, ObjectId]
  with BaseMongoDAO[ConfigSection] {

  val dao = new SalatDAO[ConfigSection, ObjectId](collection = mongoCollection("configs")) {}
  final protected val keyFieldName = "name"

  def upsertSection(o: ConfigSection): Unit = upsert(o.name, o)

  def deleteSection(name: String): Unit = delete(name)

  def readConfig: Configuration = {
    val a: List[ConfigSection] = List() ++ all

    Configuration(
      a.foldLeft[Map[String, ConfigSection]](Map()) { (m, v) => m + (v.name -> v) })
  }

}

