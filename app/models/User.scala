package models

import java.util.Date

import play.api.libs.json.{ Json }
import org.joda.time.DateTime

case class User(
  var _id: Option[String],
  var username: String,
  var firstname: Option[String],
  var lastname: Option[String],
  var password: Option[String],
  var email: Option[String],
  var role: Option[String],
  var token: Option[String],
  var address: Option[Seq[Address]],
  var typeUser: Option[String],
  var audit: Option[Audit]) extends GenericEntity

object TypeUser extends Enumeration {
  val C = "Customer"
  val P = "Provider"
}

object User {

  def apply() = new User(None, "", None, None, None, None, None, None, None, None, None)
  def apply(firstname: Option[String], lastname: Option[String], token: Option[String]) = new User(
    None,
    "",
    firstname,
    lastname,
    None,
    None,
    None,
    token,
    None,
    None,
    None)

  implicit object UserIdentity extends Identity[models.User, String] {
    val name = "_id"
    val counter = "users"
    val collection = "users"
    def of(entity: User): String = entity._id.getOrElse("")
    def set(entity: User): User = entity.copy(_id = entity._id)
    def clear(entity: User): User = entity.copy(_id = Some(""))
    def next(entity: User): String = entity._id.getOrElse("")
    def setCounter(entity: User, newId: String): User = entity.copy(_id = Some(newId))
    def setAudit(entity: User): User = entity.copy(audit = Some(Audit(Some(false), Some(new DateTime), Some(new DateTime))))
    def updateAudit(entity: User): User = {
      entity.audit.getOrElse(Audit(Some(false), Some(new DateTime), Some(new DateTime))).updateTS = Some(new DateTime)
      entity
    }
  }
}