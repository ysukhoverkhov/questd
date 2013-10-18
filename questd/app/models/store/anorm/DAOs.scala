package models.store.anorm

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

import models.store.DAOs._

private[store] object DAOs {

  /*
   * User
   */
  import models.domain.user._

  case class UserDB(id: Long, name: String, pass: String)
  val user = {
    get[Long]("id") ~
      get[String]("name") ~
      get[String]("pass") map {
        case id ~ name ~ pass => UserDB(id, name, pass)
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
              case None => Option(User(u.id.toString, u.name, u.pass))
              case Some(_) => rv
            }

          }
      }

    def update(t: User): Unit = {}
//      DB.withConnection { implicit c =>
//        SQL("delete from user where id = {id}").on(
//          'id -> t.id.toString.toLong).executeUpdate()
//      }

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

