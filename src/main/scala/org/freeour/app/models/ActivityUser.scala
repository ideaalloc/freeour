package org.freeour.app.models

import java.sql.Timestamp

import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by Bill Lv on 2/7/15.
 */
case class ActivityUser(id: Option[Long] = None, activityId: Long, userId: Long,
                        joinDate: Timestamp = new Timestamp(System.currentTimeMillis))

class ActivitiesUsers(tag: Tag) extends Table[ActivityUser](tag, "ACTIVITIES_USERS") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

  def activityId = column[Long]("ACTIVITY_ID", O.NotNull)

  def userId = column[Long]("USER_ID", O.NotNull)

  def joinDate = column[Timestamp]("JOIN_DATE", O.NotNull)

  def activityUserIdx = index("IDX_ACTIVITIES_USERS_ACTIVITY_USER", (activityId, userId), unique = true)

  def activityIdx = index("IDX_ACTIVITIES_USERS_ACTIVITY", activityId)

  def joinDateIdx = index("IDX_ACTIVITIES_USERS_JOIN_DATE", joinDate)

  override def * = (id.?, activityId, userId, joinDate) <>(ActivityUser.tupled, ActivityUser.unapply)
}

object ActivityUserRepository extends TableQuery(new ActivitiesUsers(_)) {
  def findByActivityId(activityId: Long)(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) =
    filter(_.activityId === activityId).sortBy(_.joinDate.asc).map(_.userId).run

  def countUsers(activityId: Long)(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) =
    filter(_.activityId === activityId).length.run

  def exists(activityId: Long, userId: Long)(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) =
    filter(p => p.activityId === activityId && p.userId === userId).exists.run

  def deleteByActivityId(activityId: Long)(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) =
    filter(_.activityId === activityId).delete
}
