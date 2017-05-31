package models

case class Error(var code: String, var description: String)

object Status extends Enumeration {
  val NOK = "NOK"
  val OK = "OK"
}

case class RestResult[T](
  var data: Seq[T],
  var errors: Seq[Error],
  var status: String
)