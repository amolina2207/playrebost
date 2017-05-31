package services

import scala.BigDecimal
import scala.annotation.implicitNotFound
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.math.BigDecimal.int2bigDecimal
import org.joda.time.DateTime
import models.Audit
import models.Counter
import models.CounterFormat
import models.User
import models.User.UserIdentity
import models.UserFormat
import play.api.libs.json.{JsObject, Json}
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import reactivemongo.api.DB
import reactivemongo.play.json.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import utils.SecurityUtils
import models.Counter.CounterIdentity
import models.Session.SessionsIdentity

import scala.util.{Failure, Success}

trait UsersService extends CRUDService[User, String] {

  def checkUserCredentials(model: models.User)(implicit ex: ExecutionContext) : Future[Option[models.User]]

  def saveSessionData(sess: models.Session)(implicit ex: ExecutionContext) : Future[Either[String, models.Session]]
  def removeSessionData(sess: models.Session)(implicit ex: ExecutionContext) : Future[Either[String, models.Session]]

  def checkIfUsernameExists(username: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    val selector  = Json.obj("username" -> username)
    val sort = Json.obj("username" -> 1)
    search(selector,sort,1) map ( ent => if(ent.isEmpty) false else true )
  }
  
}

class UsersMongoService(db: Future[DB]) extends MongoCRUDService[User, String](db) with UsersService {

  override def collection(implicit ec: ExecutionContext): Future[JSONCollection] = db.map(_.collection("users"))
  override def collectionCounters(implicit ec: ExecutionContext): Future[JSONCollection] = db.map(_.collection("counters"))
  def collectionSessions(implicit ec: ExecutionContext): Future[JSONCollection] = db.map(_.collection("sessions"))

  def checkPassword(inPass: String, dbPass: String): Boolean = {
    val inPassHash = SecurityUtils.hashMD5(inPass)
    val dbPassHash = SecurityUtils.hashMD5(dbPass)
    inPassHash==dbPassHash
  }

  def checkUserCredentials(model: models.User)(implicit ex: ExecutionContext) : Future[Option[models.User]] = {
    val emptyUser = models.User(Some(""),"",Some(""),Some(""),Some(""),Some(""),Some(""),Some(""),None,None,None)//Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))
    val dbUSer: Future[Option[models.User]] = collection.flatMap(_.find(Json.obj("username" -> model.username)).one[models.User]) map (
      _.fold(Some(emptyUser))
      (entity => Some(entity))
    )
    dbUSer.flatMap { user =>
      if(checkPassword(model.password.getOrElse(""), user.getOrElse(emptyUser).password.get)){
    	  Future.successful(Some(user.get))        
      }else{
        Future.successful(None)        
      }
    }
  }

  def removeSessionData(sess: models.Session)(implicit ex: ExecutionContext) : Future[Either[String, models.Session]] = {
    collectionSessions.flatMap(_.remove(Json.obj("username"->sess.username,"token"->sess.token.getOrElse("").toString))  map {
      case le if le.ok == true => Right(sess)
      case le => Left(le.message)
    })
  }

  def saveSessionData(sess: models.Session)(implicit ex: ExecutionContext) : Future[Either[String, models.Session]] = {
    newId(SessionsIdentity.counter).flatMap(counterResult => {
      val entityWithCounter = SessionsIdentity.setCounter(sess, counterResult.get.value.toString)
      val entityWithAudit = SessionsIdentity.setAudit(entityWithCounter)
      val selCounter = Json.obj(CounterIdentity.name -> SessionsIdentity.counter)
      collectionCounters.flatMap(_.update(selCounter, counterResult.get) flatMap { _ =>
        collectionSessions.flatMap(_.insert(Json.toJson(entityWithAudit).as[JsObject]) map {
          wr => if (wr.ok == true) Right(sess) else Left(wr.message)
        })
      })
    })
  }

  override def newId(id: String)(implicit ec: ExecutionContext): Future[Option[Counter]] = {
    collectionCounters.flatMap(_.find(Json.obj(CounterIdentity.name -> id)).one[Counter]).map (
      // Este caso no esta tratado porque habria que crear la entidad y de momento se hara de forma manual
      _.fold(Option(Counter(Some(""),BigDecimal("1"),Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))))
      (entity => Option(entity.copy(value = entity.value.+(1))))
    )
  }
}