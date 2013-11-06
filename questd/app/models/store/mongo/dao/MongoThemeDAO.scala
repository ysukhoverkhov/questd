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

import models.domain.theme._

/**
 * This class is representing user form the database.
 */
private[mongo] case class ThemeDB(
  id: Option[String],
  text: Option[String] = None,
  comment: Option[String] = None)

/**
 * DOA for Theme objects
 */
private[mongo] class MongoThemeDAO
  extends ThemeDAO
  with ModelCompanion[ThemeDB, ObjectId]
  with BaseMongoDAO[ThemeDB, String] {

  val dao = new SalatDAO[ThemeDB, ObjectId](collection = mongoCollection("themes")) {}
  protected final val keyFieldName = "id"

  /**
   * Conversion from domain object  to db object
   */
  protected implicit def domainToDB(dom: Theme): ThemeDB = {
    ThemeDB(Some(dom.id.toString), Some(dom.text), Some(dom.comment))
  }

  /**
   * Conversion from db object to domain object
   */
  protected implicit def dbToDomain(db: ThemeDB): Theme = {
    Theme(
      unlift(db.id),
      unlift(db.text),
      unlift(db.comment))
  }

  /**
   * Create
   */
  def createTheme(o: Theme): Unit = create(o)

  /**
   * Read by id
   */
  def readThemeByID(key: ThemeID): Option[Theme] = read(key)

  /**
   * Update by id.
   */
  def updateTheme(u: Theme): Unit = update(u.id, u)

  /**
   * Delete by id
   */
    def deleteTheme(key: ThemeID): Unit = delete(key)

  /**
   * All objects
   */
  def allThemes: List[Theme] = all map { u => dbToDomain(u) }

}

