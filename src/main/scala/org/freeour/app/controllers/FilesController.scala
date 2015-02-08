package org.freeour.app.controllers

import java.io.InputStream
import java.nio.file.{Files, Paths}

import org.freeour.app.FreeourStack
import org.freeour.app.config.FileConfig
import org.freeour.app.models.{Photo, PhotoRepository, Photos}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import org.scalatra.servlet.{FileUploadSupport, MultipartConfig, SizeConstraintExceededException}

import scala.slick.driver.PostgresDriver
import scala.slick.driver.PostgresDriver.simple._

/**
 * Created by Bill Lv on 2/8/15.
 */
case class FilesController(val db: Database) extends FreeourStack with FileUploadSupport
with JValueResult with JacksonJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats
  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3 * 1024 * 1024)))

  error {
    case e: SizeConstraintExceededException =>
      -2
  }

  post("/") {
    fileParams.get("file") match {
      case Some(file) =>
        val input: InputStream = file.getInputStream
        val uploadPath = Paths.get(FileConfig.getStorePath)
        if (!Files.exists(uploadPath)) {
          Files.createDirectory(uploadPath)
        }
        val storeName: String = System.currentTimeMillis() + "_" + file.getName
        try {
          Files.copy(input, Paths.get(uploadPath.toFile.getName, storeName))
        }
        finally {
          input.close()
        }
        var photoId: Long = params.getOrElse("photoId", "-1").toLong
        db.withSession { implicit session =>
          val photo: Option[Photo] = PhotoRepository.findById(photoId)
          Files.deleteIfExists(Paths.get(FileConfig.getStorePath, photo.get.store))
          PhotoRepository.deleteById(photoId)
          val result: PostgresDriver.ReturningInsertInvokerDef[Photos#TableElementType, Long]#SingleInsertResult =
            (PhotoRepository returning PhotoRepository.map(_.id)) += Photo(None, file.getName, storeName, true)
          photoId = result.toLong
        }
        photoId

      case None =>
        0
    }
  }
}
