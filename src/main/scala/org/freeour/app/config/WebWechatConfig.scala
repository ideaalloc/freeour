package org.freeour.app.config

/**
 * Created by Bill Lv on 2/25/15.
 */
object WebWechatConfig extends BaseConfig("/web.wechat.properties") {
  def isDevMode = properties.getProperty("web.wechat.dev.mode", "false").toBoolean
}
