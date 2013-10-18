package models.store.anorm.dao

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

  case class UserDB(id: Long, name: String, pass: String, session: String)
  val user = {
    get[Long]("id") ~
      get[String]("name") ~
      get[String]("pass") ~ 
      get[Option[String]]("session") map {
        case id ~ name ~ pass ~ session => UserDB(
            id,
            name,
            pass,
            session match {
              case None => SessionID.default.toString
              case Some(id) => id
            } )
      }
  }

  object AnormUserDAO extends UserDAO {

    def create(t: User): Unit =
      DB.withConnection { implicit c =>
        SQL("insert into user (name, pass) values ({name}, {pass})").on(
          'name -> t.username, 'pass -> t.password).executeUpdate()
      }

    def read(t: User): Option[User] =
      DB.withConnection { implicit c =>
        (SQL("select * from user").on(
          'id -> t.id.toString).as(user *) foldLeft (None: Option[User])) { (rv, u) =>
            rv match {
              case None => Option(User(u.id.toString, u.name, u.pass, u.session))
              case Some(_) => rv
            }

          }
      }

    def update(t: User): Unit = 
      DB.withConnection { implicit c =>
        SQL("update user set name = {name}, pass = {pass}, session = {session} where id = {id}").on(
          'id -> t.id.toString.toLong,
          'name -> t.username,
          'pass -> t.password,
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
          User(u.id.toString, u.name, u.pass)
        }
      }

  }

}

