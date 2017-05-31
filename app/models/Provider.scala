package models

import org.joda.time.DateTime

case class Provider(
  var _id: Option[String],
  var name: String,
  var description: Option[String],
  var user: Option[String],
  var audit: Option[Audit]
) extends GenericEntity

  object Provider {

    implicit object ProviderIdentity extends Identity[Provider, String] {
      val name = "_id"
      val counter = "providers"
      val collection = "providers"
      def of(entity: Provider): String = entity._id.getOrElse("")
      def set(entity: Provider): Provider = entity.copy(_id = entity._id)
      def clear(entity: Provider): Provider = entity.copy(_id = Some(""))
      def next(entity: Provider): String = entity._id.getOrElse("")
      def setCounter(entity: Provider, newId: String): Provider = entity.copy(_id = Some(newId))
      def setAudit(entity: Provider): Provider = entity.copy(audit = Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))
      def updateAudit(entity: Provider): Provider = {
        entity.audit.getOrElse(Audit(Some(false),Some(new DateTime),Some(new DateTime))).updateTS = Some(new DateTime)
        entity
      }
    }
  }