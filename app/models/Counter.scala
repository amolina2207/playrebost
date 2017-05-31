package models

import java.util.Date
import org.joda.time.DateTime

case class Counter(
    var _id: Option[String],
    var value: BigDecimal,
    var audit: Option[Audit]
  ) extends GenericEntity

  object Counter {
    implicit object CounterIdentity extends Identity[Counter, String]{
      val name = "_id"
      val counter = "_none"
      val collection = "counters"
      def of(entity: Counter): String = entity._id.getOrElse("")
      def set(entity: Counter): Counter = entity.copy(_id = entity._id)
      def clear(entity: Counter): Counter = entity.copy(_id = Some(""))
      def next(entity: Counter): String = entity._id.getOrElse("")
      def setCounter(entity: Counter, newId: String): Counter = entity
      def setAudit(entity: Counter): Counter = entity.copy(audit = Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))
      def updateAudit(entity: Counter): Counter = {
        entity.audit.getOrElse(Audit(Some(false),Some(new DateTime),Some(new DateTime))).updateTS = Some(new DateTime)
        entity
      }
        
      
    }
  }