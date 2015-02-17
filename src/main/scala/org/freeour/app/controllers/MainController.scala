package org.freeour.app.controllers

import java.text.SimpleDateFormat

import org.freeour.app.FreeourStack
import org.freeour.app.auth.AuthenticationSupport
import org.freeour.app.models.{ActivityJson, ActivityRepository, ActivityUser, ActivityUserRepository}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.{JValueResult, JacksonJsonSupport}

import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by Bill Lv on 2/10/15.
 */
case class MainController(val db: Database) extends FreeourStack with AuthenticationSupport
with JValueResult with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  val formatter: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm")

  before() {
    requireLogin()
    contentType = formats("json")
  }

  get("/") {
    contentType = "text/html"
    ssp("/main", "layout" -> "", "userId" -> user.id.get)
  }

  get("/activities") {
    db.withSession { implicit session =>
      val activities = ActivityRepository.findAllAvailable
      activities.map(p => ActivityJson(p.id, p.title, p.address, p.description, formatter.format(p.startTime),
        p.available, p.initiator))
    }
  }

  get("/activities/:activityId/users/:userId") {
    val activityId: Long = params("activityId").toLong
    val userId: Long = params("userId").toLong
    var exists: Boolean = false
    db.withSession { implicit session =>
      exists = ActivityUserRepository.exists(activityId, userId)
    }
    response.getWriter.print(exists)
  }

  get("/activities/:activityId/users/count") {
    val activityId: Long = params("activityId").toLong
    var usersNum: Int = 0
    db.withSession { implicit session =>
      usersNum = ActivityUserRepository.countUsers(activityId)
    }
    response.getWriter.print(usersNum)
  }

  get("/activities/:activityId/userids") {
    val activityId: Long = params("activityId").toLong
    db.withSession { implicit session =>
      ActivityUserRepository.findByActivityId(activityId)
    }
  }

  post("/activities/users/:userId") {
    val activityId: Long = params("activityId").toLong
    val userId: Long = params("userId").toLong
    var status: Int = 0
    try {
      db.withSession { implicit session =>
        ActivityUserRepository += ActivityUser(None, activityId, userId)
      }
      status = 1
    }
    catch {
      case e: Throwable =>
        logger.info("", e)
        status = -1
    }
    response.getWriter.print(status)
  }
}
