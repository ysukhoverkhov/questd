package models.store.anorm.dao

import scala.language.postfixOps

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import models.store.DAOs._
import models.domain.user.SessionID
import models.domain.user.User
import models.domain.user.stringToSessionID
import models.domain.user.stringToUserID

private[anorm] object user {

  /*
   * User
   */
  import models.domain.user._

  case class UserDB(id: Long, name: String, fbid: String, session: String)
  val user = {
    get[Long]("id") ~
      get[String]("name") ~
      get[String]("fbid") ~ 
      get[Option[String]]("session") map {
        case id ~ name ~ fbid ~ session => UserDB(
            id,
            name,
            fbid,
            session match {
              case None => SessionID.default.toString
              case Some(id) => id
            } )
      }
  }

  object AnormUserDAO extends UserDAO {

    def create(t: User): Unit =
      DB.withConnection { implicit c =>
        SQL("insert into user (name, fbid) values ({name}, {fbid})").on(
          'name -> t.username, 'fbid -> t.fbid).executeUpdate()
      }

    def read(t: User): Option[User] =
      DB.withConnection { implicit c =>
        (SQL("select * from user").on(
          'id -> t.id.toString).as(user *) foldLeft (None: Option[User])) { (rv, u) =>
            rv match {
              case None => Option(User(u.id.toString, u.name, u.fbid, u.session))
              case Some(_) => rv
            }

          }
      }

    def read(sessid: SessionID): Option[User] =
      DB.withConnection { implicit c =>
        (SQL("select * from user").on(
          'id -> sessid.toString).as(user *) foldLeft (None: Option[User])) { (rv, u) =>
            rv match {
              case None => Option(User(u.id.toString, u.name, u.fbid, u.session))
              case Some(_) => rv
            }

          }
      }

    def readByFBid(fbid: String): Option[User] =
      DB.withConnection { implicit c =>
        (SQL("select * from user").on(
          'fbid -> fbid).as(user *) foldLeft (None: Option[User])) { (rv, u) =>
            rv match {
              case None => Option(User(u.id.toString, u.name, u.fbid, u.session))
              case Some(_) => rv
            }
          }
      }
    
    def update(t: User): Unit = 
      DB.withConnection { implicit c =>
        SQL("update user set name = {name}, fbid = {fbid}, session = {session} where id = {id}").on(
          'id -> t.id.toString.toLong,
          'name -> t.username,
          'fbid -> t.fbid,
          'session -> t.session.toString).executeUpdate()
      }

    def delete(t: User): Unit =
      DB.withConnection { implicit c =>
        SQL("delete from user where id = {id}").on(
          'id -> t.id.toString.toLong).executeUpdate()
      }

    def all: List[User] =
      DB.withConnection { implicit c =>
        SQL("select * from user").as(user *) map { u =>
          User(u.id.toString, u.name, u.fbid, u.session)
        }
      }

  }

}

