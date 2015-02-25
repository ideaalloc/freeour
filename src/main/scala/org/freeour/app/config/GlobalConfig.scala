package org.freeour.app.config

/**
 * Created by Bill Lv on 2/25/15.
 */
object GlobalConfig extends BaseConfig("/global.properties") {
  def isDevMode = properties.getProperty("global.dev.mode", "false").toBoolean
}
