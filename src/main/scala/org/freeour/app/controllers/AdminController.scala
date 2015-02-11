package org.freeour.app.controllers

import java.sql.Timestamp

import org.freeour.app.FreeourStack
import org.freeour.app.auth.AuthenticationSupport
import org.freeour.app.models._
import org.joda.time._
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.{JValueResult, JacksonJsonSupport}

import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by Bill Lv on 2/11/15.
 */
case class AdminController(val db: Database) extends FreeourStack with AuthenticationSupport
with JValueResult with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

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
      activities
    }
  }

  post("/data/activities") {
    val dateString: String = params("startTime").replaceAll("\"", "")
    val dtUTC = new DateTime(dateString, DateTimeZone.forID("Asia/Shanghai"))
    var status: Int = 0
    try {
      db.withTransaction { implicit session =>
        val activityId = (ActivityRepository returning ActivityRepository.map(_.id)) +=
          Activity(None, params("title"), params("address"), params.getOrElse("description", ""),
            new Timestamp(dtUTC.getMillis()),
            params("available").toInt match {
              case 1 => true
              case _ => false
            },
            user.id.get)
        ActivityStatsRepository += ActivityStats(None, activityId.toLong, Some(1), None)
      }
      status = 1
    }
    catch {
      case e: Throwable =>
        logger.info("Save activity error", e)
        status = -1
    }
    status
  }
}
