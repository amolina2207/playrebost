package utils

/**
  * Created by root on 2/10/16.
  */
trait SecurityUtils {

}

object SecurityUtils {

  def hashMD5(s: String) = {
    val m = java.security.MessageDigest.getInstance("MD5")
    val b = s.getBytes("UTF-8")
    m.update(b, 0, b.length)
    new java.math.BigInteger(1, m.digest()).toString(16)
  }

}