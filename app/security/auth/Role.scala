package security.auth

import play.api.libs.json.{ Json, Format }
import reactivemongo.bson.Macros.Annotations.Ignore
import java.beans.Transient

sealed trait Role

object Role {

  case object Administrator extends Role
  case object NormalUser extends Role

  // It only says if the role is admin or normal and returns such object type
  def valueOf(value: String): Role = value match {
    case "Administrator" => Administrator
    case "NormalUser"    => NormalUser
    case _ => throw new IllegalArgumentException()
  }
  

  
//  implicit val typeBinder: TypeBinder[Role] = TypeBinder.string.map(valueOf)

}