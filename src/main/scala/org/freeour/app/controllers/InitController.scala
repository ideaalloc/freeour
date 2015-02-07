package org.freeour.app.controllers

import org.freeour.app.models._
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

  val ddl = UserRepository.ddl ++ ActivityRepository.ddl ++ ActivityUserRepository.ddl ++ ActivityStatsRepository.ddl

  get("/create-tables") {
    db withDynSession {
      ddl.create
    }
  }

  get("/load-data") {
    db withDynSession {
      UserRepository ++= Seq(
        User(None, "ideaalloc@gmail.com", BCrypt.hashpw("888888", BCrypt.gensalt()),
          "Bill", Some("13888888888"), true, Some("/path/avatar.png"))
      )
      Unit
    }
  }

  get("/drop-tables") {
    db withDynSession {
      ddl.drop
    }
  }

  get("/users") {
    db withDynSession {
      val q3 = for {
        u <- UserRepository
      } yield (u.email.asColumnOf[String])

      contentType = "text/html"
      q3.list mkString "<br>"
    }
  }

}