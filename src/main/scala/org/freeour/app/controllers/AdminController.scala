package org.freeour.app.controllers

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import org.freeour.app.FreeourStack
import org.freeour.app.auth.AuthenticationSupport
import org.freeour.app.models._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.Ok
import org.scalatra.json.{JValueResult, JacksonJsonSupport}

import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by Bill Lv on 2/11/15.
 */
case class AdminController(val db: Database) extends FreeourStack with AuthenticationSupport
with JValueResult with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  val formatter: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm")

  before() {
    requireLogin()
    if (!user.isAdmin) {
      halt(403, ssp("/errors/error403", "layout" -> ""))
    }
    contentType = formats("json")
  }

  get("/") {
    contentType = "text/html"
    ssp("/admin/index", "layout" -> "", "userId" -> user.id.get)
  }

  get("/data/activities") {
    db.withSession { implicit session =>
      val activities: List[Activities#TableElementType] = ActivityRepository.list
      activities.map(p => ActivityJson(p.id, p.title, p.address, p.description, formatter.format(p.startTime),
        p.available, p.initiator))
    }
  }

  post("/data/activities") {
    val dateString: String = params("startTimeString").replaceAll("\"", "")
    val parsedDate: Date = formatter.parse(dateString)

    var status: Int = 0
    val id: Long = params.getOrElse("id", "-1").toLong
    val activity: Activity = Activity(None, params("title"), params("address"), params.getOrElse("description", ""),
      new Timestamp(parsedDate.getTime),
      params("available").toInt match {
        case 1 => true
        case _ => false
      },
      user.id.get)
    try {
      db.withTransaction { implicit session =>
        if (id == -1) {
          val activityId = (ActivityRepository returning ActivityRepository.map(_.id)) += activity
          ActivityStatsRepository += ActivityStats(None, activityId.toLong, Some(1), None)
        } else {
          activity.id = Some(id)
          ActivityRepository.update(activity)
        }
      }
      status = 1
    }
    catch {
      case e: Throwable =>
        logger.info("Save activity error", e)
        status = -1
    }
    Ok(response.getWriter.print(status))
  }

}
