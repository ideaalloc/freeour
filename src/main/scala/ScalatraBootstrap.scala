import javax.servlet.ServletContext

import com.mchange.v2.c3p0.ComboPooledDataSource
import org.freeour.app.controllers._
import org.scalatra._
import org.slf4j.LoggerFactory

import scala.slick.jdbc.JdbcBackend.Database

class ScalatraBootstrap extends LifeCycle {
  val logger = LoggerFactory.getLogger(getClass)

  val cpds = new ComboPooledDataSource
  logger.info("Created c3p0 connection pool")

  override def init(context: ServletContext) {
    val db = Database.forDataSource(cpds)
    context.mount(InitController(db), "/db/*")
    context.mount(new MainController(db), "/*")
    context.mount(new SessionsController(db), "/sessions/*")
    context.mount(new UsersController(db), "/users/*")
    context.mount(new AdminController(db), "/admin/*")
  }

  private def closeDbConnection() {
    logger.info("Closing c3po connection pool")
    cpds.close
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    closeDbConnection
  }
}
