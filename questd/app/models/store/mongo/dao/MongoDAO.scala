package models.store.mongo.dao

import scala.language.postfixOps
import scala.language.implicitConversions
import play.api.db._
import play.api.Play.current
import models.store.DAOs._
import models.domain.user._
import play.api.Play.current
import java.util.Date
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import se.radley.plugin.salat._
import models.store.mongo.SalatContext._
import play.Logger
import com.mongodb.CommandResult
import com.mongodb.DBObject

// TODO: write test case for me.

private[mongo] case class UserDB(
  userid: Option[String] = None,
  fbid: Option[String] = None,
  session: Option[String] = None)

private[mongo] object user {


  implicit def domainToDB(dom: User): UserDB = {
    val sess = dom.session match {
      case None => None
      case Some(v) => Some(v.toString)
    }

    UserDB(Some(dom.id.toString()), dom.fbid, sess)
  }

  implicit def dbToDomain(db: UserDB): User = {
    val ses = db.session match {
      case None => None
      case Some(s) => Some(SessionID(s))
    }
    val uid: UserID = db.userid match {
      case None => UserID.default
      case Some(s) => s
    } 

    User(uid, db.fbid, ses)
  }


  /**
   * 
   */
  object MongoUserDAO extends UserDAO with ModelCompanion[UserDB, ObjectId] {

    val dao = new SalatDAO[UserDB, ObjectId](collection = mongoCollection("users")) {}

    def create(u: User): Unit = {
      Logger.debug("Creating user in database " + u.toString)
      
      val wr = save(u)
      if (!wr.getLastError().ok) {
        // TODO throw exception here
        Logger.error("Unable to save user to db " + wr.toString())
      } else {
        Logger.debug("User saved to db successfuly " + u.toString)
      }
      
    }
    
    private def makeQueryObject(user: UserDB): DBObject = {
      // TODO replace it with custom object to map converter what makes unlifting 

      val js = toCompactJson(user)
      val dbo = com.mongodb.util.JSON.parse(js).asInstanceOf[DBObject]
      
      dbo
    }

    private def find(user: UserDB): Option[User] = {
      findOne(makeQueryObject(user)) match {
        case None => None
        case Some(o) => Some(o)
      }
    }

    def read(u: User): Option[User] = {
      find(UserDB(Some(u.id.toString), None, None))
    }

    def readBySessionID(sessid: SessionID): Option[User] = {
      find(UserDB(None, None, Some(sessid.toString)))
    }

    def readByFBid(fbid: String): Option[User] = {
      find(UserDB(None, Some(fbid), None))
    }

    def update(u: User): Unit = {
      
      Logger.debug("Updating user in database " + u.toString)
      
      val q = makeQueryObject(UserDB(Some(u.id.toString), None, None))
      val dbo = toDBObject(u)
      val wr = dao.update(q, dbo, false, false)
      
      if (!wr.getLastError().ok) {
        // TODO throw exception here and remove logging. Login should be done only in place of exception handling.
        Logger.error("Unable to update user to db " + wr.toString())
      } else {
        Logger.debug("User update in db successfuly " + u.toString)
      }
    }

    def delete(u: User): Unit = {
      val wr = remove(makeQueryObject(UserDB(Some(u.id.toString), None, None)))

      // TODO deal with wr
    }

    def all: List[User] = {
      // TODO this will be very slow and will fetch everything.
      List() ++ find(MongoDBObject()) map { u => dbToDomain(u) }
    }
  }

}

