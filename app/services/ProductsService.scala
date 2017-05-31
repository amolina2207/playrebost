package services

import actors.CRUDActor
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import models.Counter.CounterIdentity
import models.Product.ProductIdentity
import models.{Audit, Counter, CounterFormat, Find, GenericEntity, Product, ProductFormat}
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import reactivemongo.api.DB
import reactivemongo.play.json.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

trait ProductsService extends CRUDService[Product, String] {

  def search(parameters: Find)(implicit ec: ExecutionContext): Future[Traversable[Product]] = {
    val selector  = Json.obj("name" -> Json.obj("$regex" ->  (".*" + parameters.search.getOrElse("") + ".*"), "$options"->"-i"))
    val selTags = Json.obj("tags" -> Json.obj("$in"->Json.arr(parameters.tags.mkString(","))))
    val sort = Json.obj(parameters.sort.head.toString -> parameters.sort_dir)
    search(selector,sort,parameters.limit,parameters.lang)
  }

  def searchByProvider(idProvider: String, parameters: Find)(implicit ec: ExecutionContext): Future[Traversable[Product]] = {
    val selector  = Json.obj("name" -> Json.obj("$regex" ->  (".*" + parameters.search.getOrElse("") + ".*"), "$options"->"-i"),"provider"->idProvider)
    val sort = Json.obj(parameters.sort.head.toString -> parameters.sort_dir)
    search(selector,sort,parameters.limit,parameters.lang)
  }
}

class ProductsMongoService(db: Future[DB]) extends MongoCRUDService[Product, String](db) with ProductsService {

  override def collection(implicit ec: ExecutionContext): Future[JSONCollection] = db.map(_.collection("products"))
  override def collectionCounters(implicit ec: ExecutionContext): Future[JSONCollection] = db.map(_.collection("counters"))

  override def newId(id: String)(implicit ec: ExecutionContext): Future[Option[Counter]] = {
    collectionCounters.flatMap(_.find(Json.obj(CounterIdentity.name -> id)).one[Counter]).map (
      // Este caso no esta tratado porque habria que crear la entidad y de momento se hara de forma manual
      _.fold(Option(Counter(Some(""),BigDecimal("1"),Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))))
      (entity => Option(entity.copy(value = entity.value.+(1))))
    )
  }

  override def translateEntity(entity: Future[Option[Product]], lang: String)(implicit ec: ExecutionContext): Future[Option[Product]] = {
    entity.map(innerEntity => {
      if (innerEntity.isDefined) {
        Option(innerEntity.get.translate(innerEntity.get, lang))
      } else {
        innerEntity
      }
    })
  }

  override def translateEntities(entities: Future[Traversable[Product]], lang: String)(implicit ec: ExecutionContext): Future[Traversable[Product]] = {
    entities.map(prods => prods.map(pro => pro.translate(pro,lang)))
  }
}