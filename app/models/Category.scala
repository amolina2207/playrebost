package models

import org.joda.time.DateTime

case class Category (
    var _id: Option[String],
    var name: String,
    var description: Option[String],
    var parent: Option[String],
    var audit: Option[Audit]
) extends GenericEntity

  object Category {

    implicit object CategoryIdentity extends Identity[Category, String] {
      val name = "_id"
      val counter = "categories"
      val collection = "categories"
      def of(entity: Category): String = entity._id.getOrElse("")
      def set(entity: Category): Category = entity.copy(_id = entity._id)
      def clear(entity: Category): Category = entity.copy(_id = Some(""))
      def next(entity: Category): String = entity._id.getOrElse("")
      def setCounter(entity: Category, newId: String): Category = entity.copy(_id = Some(newId))
      def setAudit(entity: Category): Category = entity.copy(audit = Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))
      def updateAudit(entity: Category): Category = {
        entity.audit.getOrElse(Audit(Some(false),Some(new DateTime),Some(new DateTime))).updateTS = Some(new DateTime)
        entity
      }
    }
  }