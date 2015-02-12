package org.freeour.app.models

import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by Bill Lv on 2/7/15.
 */
case class ActivityStats(id: Option[Long] = None, activityId: Long, membersNum: Option[Int], fee: Option[Double])

class ActivitiesStats(tag: Tag) extends Table[ActivityStats](tag, "ACTIVITIES_STATS") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

  def activityId = column[Long]("ACTIVITY_ID", O.NotNull)

  def membersNum = column[Int]("MEMBERS_NUM", O.Nullable)

  def fee = column[Double]("FEE", O.Nullable)

  def activityIdx = index("IDX_ACTIVITIES_STATS_ACTIVITY", activityId, unique = true)

  override def * = (id.?, activityId, membersNum.?, fee.?) <>(ActivityStats.tupled, ActivityStats.unapply)
}

object ActivityStatsRepository extends TableQuery(new ActivitiesStats(_)) {
  def deleteByActivityId(activityId: Long)(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) =
    filter(_.activityId === activityId).delete
}
