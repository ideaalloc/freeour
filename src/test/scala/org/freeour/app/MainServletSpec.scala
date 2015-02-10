package org.freeour.app

import org.freeour.app.controllers.MainController
import org.scalatra.test.specs2._

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html
class MainServletSpec extends ScalatraSpec {
  def is =
    "GET / on ProtectedServlet" ^
      "should return status 200" ! root302 ^
      end

  addServlet(classOf[MainController], "/*")

  def root302 = get("/") {
    status must_== 302
  }
}
