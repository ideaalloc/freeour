package org.freeour.app.config

/**
 * Created by Bill Lv on 2/8/15.
 */
object FileConfig extends BaseConfig("/file.properties") {
  def getStorePath = properties.getProperty("file.upload.store.path")
}
