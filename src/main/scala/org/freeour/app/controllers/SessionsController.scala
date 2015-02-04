package org.freeour.app.controllers

import org.freeour.app.FreeourStack
import org.freeour.app.auth.AuthenticationSupport

import scala.slick.driver.PostgresDriver.simple._

case class SessionsController(val db: Database) extends FreeourStack with AuthenticationSupport {

  before("/new") {
    logger.info("SessionsController: checking whether to run RememberMeStrategy: " + !isAuthenticated)

    if (!isAuthenticated) {
      scentry.authenticate("RememberMe")
    }
  }

  get("/new") {
    if (isAuthenticated) redirect("/")

    contentType = "text/html"
    ssp("/sessions/login", "layout" -> "")
  }

  post("/") {
    scentry.authenticate()

    if (isAuthenticated) {
      redirect("/")
    } else {
      redirect("/sessions/new")
    }
  }

  // Never do this in a real app. State changes should never happen as a result of a GET request. However, this does
  // make it easier to illustrate the logout code.
  get("/logout") {
    scentry.logout()
    redirect("/")
  }

}