package models

import org.joda.time.DateTime

case class OrderLine(
    var _id: Option[String],
    var product: Option[String],
    var price: Option[BigDecimal],
    var dto: Option[BigDecimal],
    var typeTax: Option[BigDecimal],
    var porTax: Option[BigDecimal],
    var totalTax: Option[BigDecimal],
    var weight: Option[BigDecimal],
    var total: Option[BigDecimal],
    var order: Option[String],
    var audit: Option[Audit]
) extends GenericEntity

  object OrderLine {

    implicit object OrderLineIdentity extends Identity[OrderLine, String] {
      val name = "_id"
      val counter = "lines"
      val collection = "lines"
      def of(entity: OrderLine): String = entity._id.getOrElse("")
      def set(entity: OrderLine): OrderLine = entity.copy(_id = entity._id)
      def clear(entity: OrderLine): OrderLine = entity.copy(_id = Some(""))
      def next(entity: OrderLine): String = entity._id.getOrElse("")
      def setCounter(entity: OrderLine, newId: String): OrderLine = entity.copy(_id = Some(newId))
      def setAudit(entity: OrderLine): OrderLine = entity.copy(audit = Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))
      def updateAudit(entity: OrderLine): OrderLine = {
        entity.audit.getOrElse(Audit(Some(false),Some(new DateTime),Some(new DateTime))).updateTS = Some(new DateTime)
        entity
      }
    }
  }