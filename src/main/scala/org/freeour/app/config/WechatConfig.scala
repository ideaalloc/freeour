package org.freeour.app.config

/**
 * Created by Bill Lv on 2/25/15.
 */
object WechatConfig extends BaseConfig("/wechat.properties") {
  def getApiAccessToken = properties.getProperty("wechat.api.access.token")
  def getAppid = properties.getProperty("wechat.appid")
  def getSecret = properties.getProperty("wechat.secret")
}
