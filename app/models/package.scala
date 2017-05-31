
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._
import play.api.libs.functional.syntax._
import reactivemongo.play.json.collection.JSONCollection

package object models {

  // Json transformers

//  implicit val dateTimePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ"
//  implicit val externalDateTimeReads = Reads.jodaDateReads(dateTimePattern)
//  implicit val externalDateTimeWrites = Writes.jodaDateWrites(dateTimePattern)
  
//  implicit val dateTimeReads = new Reads[DateTime] {
//    def reads(jv: JsValue) = {
//      jv match {
//        case JsObject(Seq(("$date", JsNumber(millis)))) =>
//          { 
//            JsSuccess(new DateTime(millis.toLong))
//          }
//        case _ => throw new Exception(s"Unknown JsValue for DateTime: $jv")
//      }
//    }
//  }
//
//  implicit val dateTimeReads = new Reads[DateTime] {
//    def reads(jv: JsValue) = {
//     jv match {
//        case JsObject(Seq("$date", JsNumber(milis))) => JsSuccess(new DateTime(milis.toLong))
//        case _ => throw new Exception(s"Unknown JsValue for DateTime: $jv")
//      }
//    }
//  }
//  
//  implicit val dateTimeWrites = new Writes[DateTime] {
//    def writes(dt: DateTime): JsValue = {
//      Json.toJson(BSONDateTime(dt.getMillis)) // {"$date": millis}
//    }
//  }
  
//  implicit val yourJodaDateReads = Reads.jodaDateReads("yyyy-MM-dd'T'HH:mm:ss'Z'")
//  implicit val yourJodaDateWrites = Writes.jodaDateWrites("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit val AuditFormat = Json.format[Audit]
  implicit val AddressFormat = Json.format[Address]
  implicit val OrderFormat = Json.format[Order]
  implicit val OrderLineFormat = Json.format[OrderLine]
  implicit val CounterFormat = Json.format[Counter]
  implicit val ErrorFormat = Json.format[Error]
  implicit val SearchFormat = Json.format[SearchQuery]
  implicit val ProviderInfoFormat = Json.format[Provider]
  implicit val TagFormat = Json.format[Tag]
  implicit val ProductFormat = Json.format[Product]
  implicit val CategoryFormat = Json.format[Category]
  implicit val UserFormat = Json.format[User]
  implicit val SessionFormat = Json.format[Session]
  //implicit val ControlledUpdateFormat = Json.format[ControlledUpdate]

  implicit def restResultWrites[T](implicit fmt: Writes[T]) = new Writes[RestResult[T]]{
    def writes(ts: RestResult[T]) = JsObject(Seq(
      "data" -> JsArray(ts.data.map(Json.toJson(_))),
      "errors" -> JsArray(ts.errors.map(Json.toJson(_))),
      "status" -> JsString(ts.status)
    ))
  }




}
