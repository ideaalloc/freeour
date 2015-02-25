package org.freeour.app.auth.strategies

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.freeour.app.models.{User, UserRepository}
import org.mindrot.jbcrypt.BCrypt
import org.scalatra.ScalatraBase
import org.scalatra.auth.ScentryStrategy
import org.slf4j.LoggerFactory

import scala.slick.driver.MySQLDriver.simple._

class UserPasswordStrategy(protected val app: ScalatraBase, protected val db: Database)(
  implicit request: HttpServletRequest, response: HttpServletResponse)
  extends ScentryStrategy[User] {

  val logger = LoggerFactory.getLogger(getClass)

  override def name: String = "UserPassword"

  private def login = app.params.getOrElse("login", "")

  private def password = app.params.getOrElse("password", "")


  /** *
    * Determine whether the strategy should be run for the current request.
    */
  override def isValid(implicit request: HttpServletRequest) = {
    logger.info("UserPasswordStrategy: determining isValid: " + (login != "" && password != "").toString())
    login != "" && password != ""
  }

  /**
   * In real life, this is where we'd consult our data store, asking it whether the user credentials matched
   * any existing user. Here, we'll just check for a known login/password combination and return a user if
   * it's found.
   */
  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {
    logger.info("UserPasswordStrategy: attempting authentication")

    var user: Option[User] = None

    db withSession {
      implicit session =>
        user = UserRepository.findByEmail(login.toLowerCase)
    }

    if (user != None && BCrypt.checkpw(password, user.get.password)) {
      logger.info("UserPasswordStrategy: login succeeded")
      user
    } else {
      logger.info("UserPasswordStrategy: login failed")
      None
    }
  }

  /**
   * What should happen if the user is currently not authenticated?
   */
  override def unauthenticated()(implicit request: HttpServletRequest, response: HttpServletResponse) {
    app.redirect("/sessions/new")
  }

}

