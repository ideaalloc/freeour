import com.mojolly.scalate.ScalatePlugin.ScalateKeys._
import com.mojolly.scalate.ScalatePlugin._
import org.scalatra.sbt._
import sbt.Keys._
import sbt._

object FreeourBuild extends Build {
  val Organization = "org.freeour"
  val Name = "Freeour"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.4"
  val ScalatraVersion = "2.3.0"
  val JettyVersion = "9.1.5.v20140505"

  lazy val project = Project(
    "freeour",
    file("."),
    settings = ScalatraPlugin.scalatraWithJRebel ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-auth" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.1.1" % "runtime",
        "org.eclipse.jetty" % "jetty-plus" % JettyVersion % "container;provided",
        "org.eclipse.jetty" % "jetty-webapp" % JettyVersion % "container",
        "org.eclipse.jetty.websocket" % "websocket-server" % JettyVersion % "container;provided",
        "com.typesafe.slick" %% "slick" % "2.1.0",
        "c3p0" % "c3p0" % "0.9.1.2",
        "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
        "org.mindrot" % "jbcrypt" % "0.3m",
        "org.scalatra" %% "scalatra-json" % "2.3.0",
        "org.json4s" %% "json4s-jackson" % "3.2.11",
        "com.sksamuel.scrimage" %% "scrimage-core" % "1.4.2",
        "com.sksamuel.scrimage" %% "scrimage-canvas" % "1.4.2",
        "com.sksamuel.scrimage" %% "scrimage-filters" % "1.4.2",
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "container;provided;test" artifacts Artifact("javax.servlet-api", "jar", "jar")
      ),

      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile) { base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty, /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
            ), /* add extra bindings here */
            Some("templates")
          )
        )
      }
    )
  )
}
