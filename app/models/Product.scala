package models

import org.joda.time.DateTime

case class Product(
  var _id: Option[String],
  var name: String,
  var loc_names: Option[Map[String,String]],
  var status: Option[String],
  var image: Option[String],
  var provider: Option[String],
  var tags: Option[Seq[String]], // Sequence of ids that point tags
  var audit: Option[Audit]) extends GenericEntity with MultiLang {

  def translate(entity : Product, lang: String): Product = {
    var newName : String = entity.name
    if(entity.loc_names.isDefined && entity.loc_names.get.exists(_._1 == (lang))){
      newName = entity.loc_names.get(lang)
    }
    entity.copy(name = newName)
  }
}

object Product {

  def apply(name: String) = new Product(None, name, None, None, None, None, None, None)

  implicit object ProductIdentity extends Identity[Product, String] {
    val name = "_id"
    val counter = "products"
    val collection = "products"
    def of(entity: Product): String = entity._id.getOrElse("")
    def set(entity: Product): Product = entity.copy(_id = entity._id)
    def clear(entity: Product): Product = entity.copy(_id = Some(""))
    def next(entity: Product): String = entity._id.getOrElse("")
    def setCounter(entity: Product, newId: String): Product = entity.copy(_id = Some(newId))
    def setAudit(entity: Product): Product = entity.copy(audit = Some(Audit(Some(false), Some(new DateTime), Some(new DateTime))))
    def updateAudit(entity: Product): Product = {
      entity.audit.getOrElse(Audit(Some(false), Some(new DateTime), Some(new DateTime))).updateTS = Some(new DateTime)
      entity
    }
  }

}