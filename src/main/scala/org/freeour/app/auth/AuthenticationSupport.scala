package org.freeour.app.auth

import org.freeour.app.auth.strategies.{RememberMeStrategy, UserPasswordStrategy}
import org.freeour.app.models.{User, UserRepository}
import org.scalatra.ScalatraBase
import org.scalatra.auth.{ScentryConfig, ScentrySupport}
import org.slf4j.LoggerFactory

import scala.slick.driver.MySQLDriver.simple._


trait AuthenticationSupport extends ScalatraBase with ScentrySupport[User] {
  self: ScalatraBase =>

  protected def fromSession = {
    case id: String =>
      db withSession {
        implicit session =>
          UserRepository.findById(id.toLong).get
      }
  }

  protected def toSession = {
    case usr: User => usr.id.get.toString
  }

  protected val scentryConfig = (new ScentryConfig {
    override val login = "/sessions/new"
  }).asInstanceOf[ScentryConfiguration]

  val logger = LoggerFactory.getLogger(getClass)

  val db: Database

  protected def requireLogin() = {
    if (!isAuthenticated) {
      redirect(scentryConfig.login)
    }
  }

  /**
   * If an unauthenticated user attempts to access a route which is protected by Scentry,
   * run the unauthenticated() method on the UserPasswordStrategy.
   */
  override protected def configureScentry = {
    scentry.unauthenticated {
      scentry.strategies("UserPassword").unauthenticated()
    }
  }

  /**
   * Register auth strategies with Scentry. Any controller with this trait mixed in will attempt to
   * progressively use all registered strategies to log the user in, falling back if necessary.
   */
  override protected def registerAuthStrategies = {
    scentry.register("UserPassword", app => new UserPasswordStrategy(app, db))
    scentry.register("RememberMe", app => new RememberMeStrategy(app, db))
  }

}