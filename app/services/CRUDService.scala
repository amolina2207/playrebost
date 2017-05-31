package services

import actors.CRUDActor
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import models.Counter.CounterIdentity
import models.{Counter, GenericEntity, Identity, UtilsID}
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.{Format, JsObject, Json}
import reactivemongo.api.{DB, ReadPreference}
import utils.DefaultValues._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

case class UpdateMsg[E](entity : E)

trait CRUDService[E, ID] {
  def read(id: ID, lang: String = DEFAULT_LANG.head)(implicit ec: ExecutionContext): Future[Option[E]]
  def newId(id: String)(implicit ec: ExecutionContext): Future[Option[Counter]]
  def create(entity: E)(implicit ec: ExecutionContext): Future[Either[String, E]]
  def update(entity: E)(implicit ec: ExecutionContext): Future[Either[models.Error, E]]
  def delete(id: ID)(implicit ec: ExecutionContext): Future[Either[String, ID]]
  def search(selector: JsObject, sort: JsObject, limit: Int, lang: String = DEFAULT_LANG.head)(implicit ec: ExecutionContext): Future[Traversable[E]]
}

abstract class MongoCRUDService[E: Format, ID: Format](db: Future[DB])(implicit identity: Identity[E, ID]) extends CRUDService[E, ID] {

  import reactivemongo.play.json._
  import reactivemongo.play.json.collection.JSONCollection

  def collection(implicit ec: ExecutionContext): Future[JSONCollection]
  def collectionCounters(implicit ec: ExecutionContext): Future[JSONCollection]

  val CRUD_ACTOR_SYSTEM = ActorSystem("crudactorsystem")

  val myActor = CRUD_ACTOR_SYSTEM.actorOf(Props(new CRUDActor[E,ID](db)), name = "myActor")

  def translateEntity(entity: Future[Option[E]], lang: String)(implicit ec: ExecutionContext) : Future[Option[E]] = entity
  def translateEntities(entities: Future[Traversable[E]], lang: String)(implicit ec: ExecutionContext) : Future[Traversable[E]] = entities

  def read(id: ID, lang: String = DEFAULT_LANG.head)(implicit ec: ExecutionContext): Future[Option[E]] = {
    val result = collection.flatMap(_.find(Json.obj(identity.name -> id)).one[E])
    translateEntity(result,lang)
  }

  def delete(id: ID)(implicit ec: ExecutionContext): Future[Either[String, ID]] = collection.flatMap(_.remove(Json.obj(identity.name -> id)) map {
    case le if le.ok == true => Right(id)
    case le => Left(le.message)
  })

  def update(entity: E)(implicit ec: ExecutionContext): Future[Either[models.Error, E]] = {

    implicit val timeout = Timeout(10 seconds)

    val future2 = myActor ? UpdateMsg[E](entity)

    val result2 = Await.result(future2, timeout.duration)

    println(result2)

    //CRUD_ACTOR_SYSTEM.stop(myActor)

    if(result2.isInstanceOf[models.Error]){
      Future.successful(Left(result2.asInstanceOf[models.Error]))
    }else{
      Future.successful(Right(result2.asInstanceOf[E]))
    }
  }

  def checkIfDatesAreEquals(entity: E)(implicit ec: ExecutionContext): Future[Boolean] = {
    val entityRequest = entity.asInstanceOf[GenericEntity]
    read(entityRequest._id.get.asInstanceOf[ID]).map {
      entityDB => {
        val dateBD = entityDB.get.asInstanceOf[GenericEntity].audit.get.updateTS
        val dateReq = entityRequest.audit.get.updateTS
        (dateBD==dateReq)
      }
    }
  }


  def create(entity: E)(implicit ec: ExecutionContext): Future[Either[String, E]] = {
    newId(identity.counter).flatMap(counterResult => {
      val entityWithCounter = identity.setCounter(entity, counterResult.get.value.toString )
      val entityWithAudit = identity.setAudit(entityWithCounter)
      val selCounter = Json.obj(CounterIdentity.name -> identity.counter)
      collectionCounters.flatMap(_.update(selCounter, counterResult.get))
        .flatMap { _ =>
          collection.flatMap(_.insert(Json.toJson(entityWithAudit).as[JsObject]).map {
            case rin if rin.ok == true => {
              Right(entityWithAudit)
            }
            case rin => Left(rin.message)
          })
        }
    })
  }

  def search(selector: JsObject, sort: JsObject, limit: Int, lang: String = DEFAULT_LANG.head)(implicit ec: ExecutionContext): Future[Traversable[E]] = {
    val result: Future[Traversable[E]] = collection.flatMap(_.find(selector)
      .sort(sort)
      .cursor[E](readPreference = ReadPreference.nearest)
      .collect[List](limit))

    translateEntities(result,lang)
  }




  /*def search(criteria: JsObject, limit: Int/*, lang: String = DEFAULT_LANG.head*/)(implicit ec: ExecutionContext): Future[Traversable[E]] =
    collection.flatMap(_.find(criteria).
      cursor[E](readPreference = ReadPreference.nearest).
      collect[List](limit))*/
      
}