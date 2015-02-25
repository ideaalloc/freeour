package org.freeour.app.config

import java.util.Properties

import scala.io.Source

/**
 * Created by Bill Lv on 2/25/15.
 */
class BaseConfig(propFileName: String) {
  protected var properties: Properties = null

  val url = getClass.getResource(propFileName)
  if (url != null) {
    val source = Source.fromURL(url)

    properties = new Properties()
    properties.load(source.bufferedReader())
  }
}
