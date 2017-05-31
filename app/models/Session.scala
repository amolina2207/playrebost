package models

import org.joda.time.DateTime

case class Session (
     var _id: Option[String],
     username: String,
     token: Option[String],
     var audit: Option[Audit]
) extends GenericEntity

object Session {

  def apply(username: String, token: Option[String]) = new Session(None,username,token,None)

  implicit object SessionsIdentity extends Identity[models.Session, String]{
    val name = "_id"
    val counter = "sessions"
    val collection = "sessions"
    def of(entity: Session): String = entity._id.getOrElse("")
    def set(entity: Session): Session = entity.copy(_id = entity._id)
    def clear(entity: Session): Session = entity.copy(_id = Some(""))
    def next(entity: Session): String = entity._id.getOrElse("")
    def setCounter(entity: Session, newId: String): Session = entity.copy(_id = Some(newId))
    def setAudit(entity: Session): Session = entity.copy(audit = Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))
    def updateAudit(entity: Session): Session = {
      entity.audit.getOrElse(Audit(Some(false),Some(new DateTime),Some(new DateTime))).updateTS = Some(new DateTime)
      entity
    }
  }
}