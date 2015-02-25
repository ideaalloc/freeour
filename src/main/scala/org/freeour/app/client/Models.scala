package org.freeour.app.client

import spray.json._

/**
 * Created by Bill Lv on 2/25/15.
 */
case class AccessToken (access_token: String, expires_in: Int)

object FreeourJsonProtocol extends DefaultJsonProtocol {
  implicit val accessTokenFormat = jsonFormat2(AccessToken)

}
