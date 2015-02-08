package org.freeour.app.models

import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by Bill Lv on 2/8/15.
 */
case class Photo(id: Option[Long] = None, name: String, store: String, enabled: Boolean)

class Photos(tag: Tag) extends Table[Photo](tag, "PHOTOS") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

  def name = column[String]("NAME", O DBType "varchar(32)", O.NotNull)

  def store = column[String]("STORE", O DBType "varchar(64)", O.NotNull)

  def enabled = column[Boolean]("ENABLED", O.NotNull, O.Default(true))

  override def * = (id.?, name, store, enabled) <>(Photo.tupled, Photo.unapply)
}

object PhotoRepository extends TableQuery(new Photos(_)) {
  def deleteById(id: Long)(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) =
    filter(_.id === id).delete

  def findById(id: Long)(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) =
    filter(_.id === id).firstOption
}
