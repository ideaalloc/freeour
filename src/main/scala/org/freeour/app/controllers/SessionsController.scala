package org.freeour.app.controllers

import java.util.NoSuchElementException

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

    val hasError: Boolean = try {
      params("error")
      true
    } catch {
      case e: NoSuchElementException =>
        logger.info("No error param")
        false
    }

    contentType = "text/html"
    ssp("/sessions/signin", "hasError" -> hasError)
  }

  post("/") {
    try {
      scentry.authenticate()
    }
    catch {
      case e: Throwable =>
        logger.info("authentication error", e)
    }

    if (isAuthenticated) {
      redirect("/")
    } else {
      redirect("/sessions/new?error")
    }
  }

  // Never do this in a real app. State changes should never happen as a result of a GET request. However, this does
  // make it easier to illustrate the logout code.
  get("/logout") {
    scentry.logout()
    redirect("/")
  }

}