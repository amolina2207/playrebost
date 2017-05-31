package models

import org.joda.time.DateTime

case class Order(
    var _id: Option[String],
    var client: Option[String],
    var address: Option[Address],
    var audit: Option[Audit]
) extends GenericEntity

object Order {

    implicit object OrderIdentity extends Identity[Order, String] {

      val name = "_id"
      val counter = "orders"
      val collection = "orders"

      def of(entity: Order): String = entity._id.getOrElse("")
      def set(entity: Order): Order = entity.copy(_id = entity._id)
      def clear(entity: Order): Order = entity.copy(_id = Some(""))
      def next(entity: Order): String = entity._id.getOrElse("")
      def setCounter(entity: Order, newId: String): Order = entity.copy(_id = Some(newId))
      def setAudit(entity: Order): Order = entity.copy(audit = Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))
      def updateAudit(entity: Order): Order = {
        entity.audit.getOrElse(Audit(Some(false),Some(new DateTime),Some(new DateTime))).updateTS = Some(new DateTime)
        entity
      }
    }
  }