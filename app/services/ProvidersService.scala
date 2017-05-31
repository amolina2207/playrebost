package services

import scala.BigDecimal
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.math.BigDecimal.int2bigDecimal
import org.joda.time.DateTime
import models.Audit
import models.Counter
import models.Counter.CounterIdentity
import models.CounterFormat
import models.Provider.ProviderIdentity
import models.Provider
import models.UserFormat
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import reactivemongo.api.DB
import reactivemongo.play.json.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection
import utils.SecurityUtils
import models.Find

trait ProvidersService extends CRUDService[Provider, String]{
  
}

class ProvidersMongoService(db: Future[DB]) extends MongoCRUDService[Provider, String](db) with ProvidersService {
  
  override def collection(implicit ec: ExecutionContext): Future[JSONCollection] = db.map(_.collection("providers"))
  override def collectionCounters(implicit ec: ExecutionContext): Future[JSONCollection] = db.map(_.collection("counters"))
 
  override def newId(id: String)(implicit ec: ExecutionContext): Future[Option[Counter]] = {
    collectionCounters.flatMap(_.find(Json.obj(CounterIdentity.name -> id)).one[Counter]).map (
      // Este caso no esta tratado porque habria que crear la entidad y de momento se hara de forma manual
      _.fold(Option(Counter(Some(""),BigDecimal("1"),Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))))
      (entity => Option(entity.copy(value = entity.value.+(1))))
    )
  }
  
}