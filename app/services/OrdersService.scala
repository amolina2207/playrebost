package services

import scala.BigDecimal
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.math.BigDecimal.int2bigDecimal
import org.joda.time.DateTime
import models.{Audit, Counter, CounterFormat, Find, Order, Provider, UserFormat}
import models.Counter.CounterIdentity
import models.Provider.ProviderIdentity
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import reactivemongo.api.DB
import reactivemongo.play.json.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import utils.SecurityUtils

trait OrdersService extends CRUDService[Order, String]{

  //    /orders/:id  --> Also add lines GET
  //    /orders/:id/cancel PUT


  //   /order-lines/:id/accept

  def search(parameters: Find)(implicit ec: ExecutionContext): Future[Traversable[Order]] = {
    val selector  = Json.obj("name" -> Json.obj("$regex" ->  (".*" + parameters.search.getOrElse("") + ".*"), "$options"->"-i"))
    val sort = Json.obj(parameters.sort.head.toString -> parameters.sort_dir)
    search(selector,sort,parameters.limit,parameters.lang)
  }


}

class OrdersMongoService(db: Future[DB]) extends MongoCRUDService[Order, String](db) with OrdersService {
  
  override def collection(implicit ec: ExecutionContext): Future[JSONCollection] = db.map(_.collection("orders"))
  override def collectionCounters(implicit ec: ExecutionContext): Future[JSONCollection] = db.map(_.collection("counters"))
 
  override def newId(id: String)(implicit ec: ExecutionContext): Future[Option[Counter]] = {
    collectionCounters.flatMap(_.find(Json.obj(CounterIdentity.name -> id)).one[Counter]).map (
      // Este caso no esta tratado porque habria que crear la entidad y de momento se hara de forma manual
      _.fold(Option(Counter(Some(""),BigDecimal("1"),Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))))
      (entity => Option(entity.copy(value = entity.value.+(1))))
    )
  }
  
}