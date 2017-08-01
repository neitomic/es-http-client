package com.github.thanhtien522.eshttpclient.entities

/**
  * @author Tien Nguyen
  */
trait AuthInfo

case class NoAuth() extends AuthInfo

case class BasicAuthInfo(user: String, passwd: String) extends AuthInfo

case class BasicAuthWithEncryptAuthInfo(user: String, passwd: String, keyStorePath: String, keyStorePass: String) extends AuthInfo
