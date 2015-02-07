package org.freeour.app.models

import java.sql.Timestamp

import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by Bill Lv on 2/7/15.
 */
case class Activity(id: Option[Long] = None, title: String, address: String, description: String, startTime: Timestamp,
                    available: Boolean, initiator: Long)

class Activities(tag: Tag) extends Table[Activity](tag, "ACTIVITIES") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

  def title = column[String]("TITLE", O DBType "varchar(32)", O.NotNull)

  def address = column[String]("ADDRESS", O DBType "varchar(64)", O.NotNull)

  def description = column[String]("DESCRIPTION", O DBType "varchar(256)", O.NotNull)

  def startTime = column[Timestamp]("START_TIME", O.NotNull)

  def available = column[Boolean]("AVAILABLE", O.NotNull, O.Default(true))

  def initiator = column[Long]("INITIATOR", O.NotNull)

  def startTimeIdx = index("IDX_ACTIVITIES_START_TIME", startTime)

  override def * = (id.?, title, address, description, startTime, available, initiator) <>
    (Activity.tupled, Activity.unapply)
}

object ActivityRepository extends TableQuery(new Activities(_)) {
  def findById(id: Long)(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) =
    filter(_.id === id).firstOption

  def findOnPage(pageNum: Int, pageSize: Int)(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) = {
    val offset: Int = (pageNum - 1) * pageSize
    sortBy(_.startTime.desc).drop(offset).take(pageSize)
  }
}