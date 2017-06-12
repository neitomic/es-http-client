/**
  * Created by user on 6/8/17.
  */
trait AuthInfo

case class NoAuth() extends AuthInfo

case class BasicAuthInfo(user: String, passwd: String) extends AuthInfo

case class BasicAuthWithEncryptAuthInfo(user: String, passwd: String, keyStorePath: String, keyStorePass: String) extends AuthInfo
