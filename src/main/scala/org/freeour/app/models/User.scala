package org.freeour.app.models

import org.slf4j.LoggerFactory

import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by Bill Lv on 2/3/15.
 */
case class User(id: Option[Long] = None, email: String, password: String, var nickname: String,
                var phone: Option[String] = None,
                isAdmin: Boolean, var avatar: Option[Long] = None) {
  val logger = LoggerFactory.getLogger(getClass)

  def forgetMe = {
    logger.info("User: this is where you'd invalidate the saved token in you User model")
  }
}

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

  def email = column[String]("EMAIL", O DBType "varchar(32)", O.NotNull)

  def password = column[String]("PASSWORD", O DBType "varchar(128)", O.NotNull)

  def nickname = column[String]("NICKNAME", O DBType "varchar(32)", O.NotNull)

  def phone = column[String]("PHONE", O DBType "varchar(32) null", O.Nullable)

  def isAdmin = column[Boolean]("IS_ADMIN", O.NotNull)

  def avatar = column[Long]("AVATAR", O.Nullable)

  def emailIdx = index("IDX_USERS_EMAIL", email, unique = true)

  override def * = (id.?, email, password, nickname, phone.?, isAdmin, avatar.?) <>(User.tupled, User.unapply)
}

object UserRepository extends TableQuery(new Users(_)) {
  def findById(id: Long)(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) =
    filter(_.id === id).firstOption

  def findByEmail(email: String)(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) =
    filter(_.email === email).firstOption

  def update(user: User)(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) = {
    filter(_.id === user.id)
      .map(p => (p.nickname, p.phone, p.isAdmin))
      .update((user.nickname, user.phone.get, user.isAdmin))
  }
}
