package org.freeour.app.config

import java.util.Properties

import scala.io.Source

/**
 * Created by Bill Lv on 2/8/15.
 */
object FileConfig {
  var properties: Properties = null

  val url = getClass.getResource("/file.properties")
  if (url != null) {
    val source = Source.fromURL(url)

    properties = new Properties()
    properties.load(source.bufferedReader())
  }

  def getStorePath = properties.getProperty("file.upload.store.path")
}
