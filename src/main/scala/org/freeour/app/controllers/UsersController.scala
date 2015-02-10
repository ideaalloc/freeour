package org.freeour.app.controllers

import java.io.{InputStream, OutputStream}
import java.nio.file.{Files, Path, Paths}
import javax.servlet.ServletException

import com.sksamuel.scrimage.{Format, Image}
import org.freeour.app.FreeourStack
import org.freeour.app.config.FileConfig
import org.freeour.app.models._
import org.json4s.{DefaultFormats, Formats}
import org.mindrot.jbcrypt.BCrypt
import org.scalatra.Ok
import org.scalatra.json.{JValueResult, JacksonJsonSupport}
import org.scalatra.servlet.{FileUploadSupport, MultipartConfig, SizeConstraintExceededException}
import org.slf4j.LoggerFactory

import scala.slick.driver.PostgresDriver
import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by Bill Lv on 2/4/15.
 */
case class UsersController(val db: Database) extends FreeourStack with FileUploadSupport
with JValueResult with JacksonJsonSupport {
  val logger = LoggerFactory.getLogger(getClass)

  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3 * 1024 * 1024)))

  protected implicit val jsonFormats: Formats = DefaultFormats

  override def isSizeConstraintException(e: Exception) = e match {
    case se: ServletException if se.getMessage.contains("exceeds max filesize") ||
      se.getMessage.startsWith("Request exceeds maxRequestSize") => true
    case _ => false
  }

  error {
    case e: SizeConstraintExceededException =>
      Ok(response.getWriter.print(-3))
  }

  get("/signup") {
    contentType = "text/html"
    ssp("/users/signup", "layout" -> "")
  }

  post("/signup") {
    val email: String = params.getOrElse("email", "")
    val password: String = params.getOrElse("password", "")
    val nickname: String = params.getOrElse("nickname", "")
    val phone: String = params.getOrElse("phone", "")
    var status: Int = 0
    var storePath: Option[Path] = None

    try {
      Option(fileMultiParams("avatar").last) match {
        case Some(file) =>
          logger.info("there is an avatar to upload....")
          val input: InputStream = file.getInputStream
          val uploadPath = Paths.get(FileConfig.getStorePath)
          if (!Files.exists(uploadPath)) {
            Files.createDirectory(uploadPath)
          }
          val originalFileName: String = file.getName
          val fileName = originalFileName.substring(0, originalFileName.lastIndexOf(".")) + ".jpg"

          val storeName: String = System.currentTimeMillis() + "_" + fileName
          logger.info("uploads root:" + uploadPath.toFile.getAbsolutePath)
          storePath = Some(Paths.get(uploadPath.toFile.getName, storeName))
          val output: OutputStream = Files.newOutputStream(storePath.get)
          try {
            Image(input).fit(50, 50).writer(Format.JPEG).withCompression(50).write(output)
          }
          finally {
            output.close
            input.close
          }
          db.withTransaction { implicit session =>
            val result: PostgresDriver.ReturningInsertInvokerDef[Photos#TableElementType, Long]#SingleInsertResult =
              (PhotoRepository returning PhotoRepository.map(_.id)) += Photo(None, fileName, storeName, true)
            val photoId: Long = result.toLong
            UserRepository += User(None, email, BCrypt.hashpw(password, BCrypt.gensalt()), nickname, Some(phone), false,
              Some(photoId))
          }
        case None =>
          logger.info("there is no avatar to upload")
          db.withTransaction { implicit session =>
            UserRepository += User(None, email, BCrypt.hashpw(password, BCrypt.gensalt()), nickname, Some(phone), false,
              None)
          }
      }
    }
    catch {
      case e: Throwable =>
        if (storePath != None)
          Files.deleteIfExists(storePath.get)
        if (e.getMessage.contains("duplicate key value violates unique constraint"))
          status = -2
        else
          status = -1
    }
    contentType = "application/json"
    Ok(response.getWriter.print(status))
  }
}
