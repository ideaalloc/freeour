package org.freeour.app.controllers

import java.io.{InputStream, OutputStream}
import java.nio.file.{Files, Path, Paths}
import javax.servlet.ServletException

import com.sksamuel.scrimage.{Format, Image}
import org.freeour.app.FreeourStack
import org.freeour.app.auth.AuthenticationSupport
import org.freeour.app.config.FileConfig
import org.freeour.app.models._
import org.json4s.{DefaultFormats, Formats}
import org.mindrot.jbcrypt.BCrypt
import org.scalatra.Ok
import org.scalatra.json.{JValueResult, JacksonJsonSupport}
import org.scalatra.servlet.{FileItem, FileUploadSupport, MultipartConfig, SizeConstraintExceededException}

import scala.slick.driver.MySQLDriver
import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by Bill Lv on 2/4/15.
 */
case class UsersController(val db: Database) extends FreeourStack with FileUploadSupport
with JValueResult with JacksonJsonSupport with AuthenticationSupport {
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
    val email: String = params.getOrElse("email", "").toLowerCase
    val password: String = params.getOrElse("password", "")
    val nickname: String = params.getOrElse("nickname", "")
    val phone: String = params.getOrElse("phone", "")
    var status: Int = 0
    var storePath: Option[Path] = None

    val avatar: Seq[FileItem] = fileMultiParams("avatar")
    var fileItem: Option[FileItem] = None
    if (avatar.length > 1) {
      fileItem = Some(avatar.last)
    } else if (avatar.length == 1) {
      fileItem = None
    }

    try {
      fileItem match {
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
            val result: MySQLDriver.ReturningInsertInvokerDef[Photos#TableElementType, Long]#SingleInsertResult =
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

  get("/:userId/avatar") {
    requireLogin()

    val userId: Long = params("userId").toLong
    var photo: Option[Photo] = None
    var download: Option[Path] = None

    db.withSession { implicit session =>
      val user = UserRepository.findById(userId).get

      val avatar: Option[Long] = user.avatar
      if (avatar == None) {
        request.getServletContext.getRealPath("/")
        download = Some(Paths.get(request.getServletContext.getRealPath("/"), "assets/images/matt.jpg"))
      } else {
        photo = PhotoRepository.findById(avatar.get)
        download = Some(Paths.get(FileConfig.getStorePath, photo.get.store))
      }
    }

    contentType = "image/jpeg"
    response.setHeader("Content-Disposition", "attachment; filename=" +
      (if (photo == None) "matt.jpg" else photo.get.name))
    download.get.toFile
  }

  get("/profile") {
    requireLogin()

    contentType = "text/html"
    ssp("/users/profile", "layout" -> "", "user" -> user)
  }

  post("/profile") {
    val userId: Long = params.getOrElse("uid", "").toLong
    val nickname: String = params.getOrElse("nickname", "")
    val phone: String = params.getOrElse("phone", "")
    var status: Int = 0
    var storePath: Option[Path] = None

    val avatar: Seq[FileItem] = fileMultiParams("avatar")
    var fileItem: Option[FileItem] = None
    if (avatar.length > 1) {
      fileItem = Some(avatar.last)
    } else if (avatar.length == 1) {
      fileItem = None
    }

    try {
      var user: Option[User] = None
      db.withSession { implicit session =>
        user = UserRepository.findById(userId)
        user.get.nickname = nickname
        user.get.phone = Some(phone)
      }

      fileItem match {
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
            deleteOriginalPhoto(user)
            val result: MySQLDriver.ReturningInsertInvokerDef[Photos#TableElementType, Long]#SingleInsertResult =
              (PhotoRepository returning PhotoRepository.map(_.id)) += Photo(None, fileName, storeName, true)
            val photoId: Long = result.toLong
            user.get.avatar = Some(photoId)

            updateUser(userId, user)
          }
        case None =>
          logger.info("there is no avatar to upload")
          db.withTransaction { implicit session =>
            updateUser(userId, user)
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

  def updateUser(userId: Long, user: Option[User])(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef): Int = {
    UserRepository.filter(_.id === userId)
      .map(p => (p.nickname, p.phone, p.avatar))
      .update((user.get.nickname, user.get.phone.get, user.get.avatar.get))
  }

  def deleteOriginalPhoto(user: Option[User])(implicit session: scala.slick.jdbc.JdbcBackend#SessionDef) {
    val avatar: Option[Long] = user.get.avatar
    if (avatar != None) {
      val photo = PhotoRepository.findById(avatar.get)
      Paths.get(FileConfig.getStorePath, photo.get.store).toFile.delete
      PhotoRepository.deleteById(photo.get.id.get)
    }
  }
}
