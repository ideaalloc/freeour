package org.freeour.app.controllers

import org.freeour.app.models.UserRepository
import org.mindrot.jbcrypt.BCrypt
import org.scalatra.ScalatraServlet

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession

/**
 * Created by Bill Lv on 2/3/15.
 */
case class InitController(val db: Database) extends ScalatraServlet with SlickRoutes

trait SlickRoutes extends ScalatraServlet {

  val db: Database

  get("/create-tables") {
    val ddl = UserRepository.users.ddl
    db withDynSession {
      ddl.create
    }
  }

  get("/load-data") {
    db withDynSession {
      UserRepository.users.map(u => (u.email, u.password, u.nickname, u.phone, u.isAdmin)) +=
        ("ideaalloc@gmail.com", BCrypt.hashpw("888888", BCrypt.gensalt()), "Bill", "13888888888", true)
    }
  }

  get("/drop-tables") {
    val ddl = UserRepository.users.ddl
    db withDynSession {
      ddl.drop
    }
  }

  get("/users") {
    db withDynSession {
      val q3 = for {
        u <- UserRepository.users
      } yield (u.email.asColumnOf[String])

      contentType = "text/html"
      q3.list mkString "<br>"
    }
  }

}