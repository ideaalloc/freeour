package org.freeour.app.controllers

import org.freeour.app.FreeourStack
import org.freeour.app.auth.AuthenticationSupport

import scala.slick.driver.PostgresDriver.simple._

case class ProtectedController(val db: Database) extends FreeourStack with AuthenticationSupport {

  /**
   * Require that users be logged in before they can hit any of the routes in this controller.
   */
  before() {
    requireLogin()
  }

  get("/") {
    "This is a protected controller action. If you can see it, you're logged in."
  }
}


