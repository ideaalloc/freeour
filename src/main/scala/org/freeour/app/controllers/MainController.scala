package org.freeour.app.controllers

import org.freeour.app.FreeourStack
import org.freeour.app.auth.AuthenticationSupport

import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by Bill Lv on 2/10/15.
 */
case class MainController(val db: Database) extends FreeourStack with AuthenticationSupport {
  before() {
    requireLogin()
  }

  get("/") {
    contentType = "text/html"
    ssp("/main", "layout" -> "", "userId" -> user.id.get)
  }
}
