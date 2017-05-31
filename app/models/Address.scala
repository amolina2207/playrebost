package models

import org.joda.time.DateTime

case class Address(
    var _id: Option[String],
    var country: Option[String],
    var province: Option[String],
    var city: Option[String],
    var postalCode: Option[String],
    var street: Option[String],
    var building: Option[String],
    var flat: Option[String],
    var door: Option[String],
    var phoneNumber: Option[String],
    var homeNumber: Option[String],
    var typeAddress: Option[String],
    var audit: Option[Audit]
) extends GenericEntity

object Address {

// def apply() = new Address(None, None, None, None, None, None, None, None, None, None, None)
  
implicit object AddressIdentity extends Identity[models.Address, String]{
      val name = "_id"
      val counter = "addresses"
      val collection = "addresses"
      def of(entity: Address): String = entity._id.getOrElse("")
      def set(entity: Address): Address = entity.copy(_id = entity._id)
      def clear(entity: Address): Address = entity.copy(_id = Some(""))
      def next(entity: Address): String = entity._id.getOrElse("")
      def setCounter(entity: Address, newId: String): Address = entity.copy(_id = Some(newId))
      def setAudit(entity: Address): Address = entity.copy(audit = Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))
      def updateAudit(entity: Address): Address = {
        entity.audit.getOrElse(Audit(Some(false),Some(new DateTime),Some(new DateTime))).updateTS = Some(new DateTime)
        entity
      }
    }
  }