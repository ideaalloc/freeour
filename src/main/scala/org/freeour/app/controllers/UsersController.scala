package org.freeour.app.controllers

import org.freeour.app.FreeourStack
import org.freeour.app.models.UserRepository
import org.mindrot.jbcrypt.BCrypt

import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by Bill Lv on 2/4/15.
 */
case class UsersController(val db: Database) extends FreeourStack {
  get("/signup") {
    contentType = "text/html"
    ssp("/users/signup", "layout" -> "")
  }

  post("/signup") {
    val email: String = params.getOrElse("email", "")
    val password: String = params.getOrElse("password", "")
    val nickname: String = params.getOrElse("nickname", "")
    val phone: String = params.getOrElse("phone", "")
    db.withSession { implicit session =>
      UserRepository.users.map(u => (u.email, u.password, u.nickname, u.phone, u.isAdmin)) +=
        (email, BCrypt.hashpw(password, BCrypt.gensalt()), nickname, phone, false)
    }
    redirect("/")
  }
}
