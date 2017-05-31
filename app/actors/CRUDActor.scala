package actors

import akka.actor.Actor
import models.{GenericEntity, Identity}
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.{Format, JsObject, Json}
import reactivemongo.api.DB
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import services.UpdateMsg

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class CRUDActor[E: Format, ID: Format](db: Future[DB])(implicit identity: Identity[E, ID]) extends Actor{

  def receive = {
    case UpdateMsg(entity) => {
      // TODO: Control if entityDB is empty and updateTS could also not be set

      val collection : Future[JSONCollection] = db.map(_.collection(identity.collection))
      val entityNew = identity.set(entity.asInstanceOf[E])
      val entityDB = collection.flatMap(_.find(Json.obj(identity.name -> entity.asInstanceOf[GenericEntity]._id)).one[E])
      val areDatesEquals : Future[Boolean] = entityDB map (r => r.get.asInstanceOf[GenericEntity].areEquals(entityNew.asInstanceOf[GenericEntity]) )
      val origSender = sender

      val responseActor = areDatesEquals map { rd =>
        if(rd){

          val entityDateUpdated = identity.updateAudit(entityNew)
          val doc = Json.toJson(entityDateUpdated).as[JsObject]
          val criteria : JsObject = Json.obj(identity.name -> entityDateUpdated.asInstanceOf[GenericEntity]._id)

          collection.flatMap(_.update(criteria, doc)) map {

            case le if le.ok == true => {
              entityDateUpdated
            }
            case le => models.Error("9991",le.message)
          }

        }else{
          models.Error("9992","The entity has been modified by a different user")
        }
      }

      responseActor onComplete {
        case Success(x) => {
          origSender ! x
        }
        case Failure(x) => {
          origSender ! models.Error("9995","Internal Error Updating - 1")
        }
      }
    }


    case _ => sender ! models.Error("9995","Internal Error Updating - 2")
  }
}