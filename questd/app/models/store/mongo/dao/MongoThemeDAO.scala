package models.store.mongo.dao

import java.util.Date
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
  id: String,
  text: String,
  comment: String)

private[mongo] object theme {

  /**
   * Conversion from domain object  to db object
   */
  implicit def domainToDB(dom: Theme): ThemeDB = {
    ThemeDB(dom.id.toString, dom.text, dom.comment)
  }

  /**
   * Conversion from db object to domain object
   */
  implicit def dbToDomain(db: ThemeDB): Theme = {
    Theme(db.id, db.text, db.comment)
  }
  
  /**
   * DOA for Theme objects
   */
  class MongoThemeDAO
    extends ThemeDAO
    with ModelCompanion[ThemeDB, ObjectId]
    with BaseDAO[ThemeDB] {

    val dao = new SalatDAO[ThemeDB, ObjectId](collection = mongoCollection("themes")) {}

    /**
     * Searches for object by query object.
     */
    private def find(user: ThemeDB): Option[Theme] = {
      findOne(makeQueryObject(user)) match {
        case None => None
        case Some(o) => Some(o)
      }
    }

    /**
     * Create
     */
    def createTheme(u: Theme): Unit = create(u)

    /**
     * Read by id
     */
    def readThemeByID(u: Theme): Option[Theme] = wrapMongoException {
      findOne(MongoDBObject("id" -> u.id.toString)) match {
        case None => None
        case Some(o) => Some(o)
      }
    }

    /**
     * Update by userid.
     */
    def updateTheme(u: Theme): Unit = wrapMongoException {

      val q = MongoDBObject("id" -> u.id.toString)
      val dbo = toDBObject(u)
      val wr = update(q, dbo, false, false)

      if (!wr.getLastError().ok) {
        throw new DatabaseException(wr.getLastError().getErrorMessage())
      }
    }

    /**
     * Delete by userid
     */

    def deleteTheme(u: Theme): Unit = wrapMongoException {
      val wr = remove(MongoDBObject("id" -> u.id.toString))

      if (!wr.getLastError().ok)
        throw new DatabaseException(wr.getLastError().getErrorMessage())
    }

    /**
     * All objects
     */
    def allThemes: List[Theme] = all map { u => dbToDomain(u) }
    
  }


}

