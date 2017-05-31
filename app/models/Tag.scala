package models

import org.joda.time.DateTime

case class Tag (
                 var _id: Option[String],
                 var name: String,
                 var loc_names: Option[Map[String,String]], // Translations of tags's names
                 var image: Option[String],
                 var tags: Option[Seq[String]], // Sequence of ids that point to others tags
                 var audit: Option[Audit]
) extends GenericEntity with MultiLang {

  def translate(entity : models.Tag, lang: String): models.Tag = {
    var newName : String = entity.name
    if(entity.loc_names.isDefined && entity.loc_names.get.exists(_._1 == (lang))){
      newName = entity.loc_names.get(lang)
    }
    entity.copy(name = newName)
  }

}

  object Tag {

    implicit object TagIdentity extends Identity[Tag, String] {
      val name = "_id"
      val counter = "tags"
      val collection = "tags"
      def of(entity: Tag): String = entity._id.getOrElse("")
      def set(entity: Tag): Tag = entity.copy(_id = entity._id)
      def clear(entity: Tag): Tag = entity.copy(_id = Some(""))
      def next(entity: Tag): String = entity._id.getOrElse("")
      def setCounter(entity: Tag, newId: String): Tag = entity.copy(_id = Some(newId))
      def setAudit(entity: Tag): Tag = entity.copy(audit = Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))
      def updateAudit(entity: Tag): Tag = {
        entity.audit.getOrElse(Audit(Some(false),Some(new DateTime),Some(new DateTime))).updateTS = Some(new DateTime)
        entity
      }
    }
  }