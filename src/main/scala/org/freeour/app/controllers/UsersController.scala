package org.freeour.app.controllers

import java.util.NoSuchElementException

import org.freeour.app.FreeourStack
import org.freeour.app.models.{User, UserRepository}
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory

import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by Bill Lv on 2/4/15.
 */
case class UsersController(val db: Database) extends FreeourStack {
  val logger = LoggerFactory.getLogger(getClass)

  get("/signup") {
    val hasError: Boolean = try {
      params("error")
      true
    } catch {
      case e: NoSuchElementException =>
        logger.info("No error param")
        false
    }

    contentType = "text/html"
    ssp("/users/signup", "layout" -> "", "hasError" -> hasError)
  }

  post("/signup") {
    val email: String = params.getOrElse("email", "")
    val password: String = params.getOrElse("password", "")
    val nickname: String = params.getOrElse("nickname", "")
    val phone: String = params.getOrElse("phone", "")
    try {
      db.withSession { implicit session =>
        UserRepository += User(None, email, BCrypt.hashpw(password, BCrypt.gensalt()), nickname, Some(phone), false, None)
      }
    }
    catch {
      case e: Throwable =>
        logger.info("save error", e)
        redirect("/users/signup?error")
    }
    redirect("/")
  }
}
