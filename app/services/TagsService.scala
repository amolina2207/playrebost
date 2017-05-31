package services

import scala.BigDecimal
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.math.BigDecimal.int2bigDecimal
import org.joda.time.DateTime
import models.{Audit, Counter, CounterFormat, Find, Product, TagFormat}
import models.Counter.CounterIdentity
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import reactivemongo.api.DB
import reactivemongo.play.json.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection

trait TagsService extends CRUDService[models.Tag, String]{
  
  def search(parameters: Find)(implicit ec: ExecutionContext): Future[Traversable[models.Tag]] = {
    val selector  = Json.obj("name" -> Json.obj("$regex" ->  (".*" + parameters.search.getOrElse("") + ".*"), "$options"->"-i"))
    val sort = Json.obj(parameters.sort.head.toString -> parameters.sort_dir)
    search(selector,sort,parameters.limit,parameters.lang)
  }
  
  def searchByParent(idParent: String, parameters: Find)(implicit ec: ExecutionContext): Future[Traversable[models.Tag]] = {
    val selector  = Json.obj("parent" -> Json.obj("$regex" ->  (".*" + idParent + ".*"), "$options"->"-i"))
    val sort = Json.obj(parameters.sort.head.toString -> parameters.sort_dir)
    search(selector,sort,parameters.limit,parameters.lang)
  }

  def childsOf(idParent: String, parameters: Find)(implicit ec: ExecutionContext): Future[Traversable[models.Tag]] = {
    val selector = Json.obj("tags" -> Json.obj("$in"->Json.arr(idParent)))
    val sort = Json.obj(parameters.sort.head.toString -> parameters.sort_dir)
    search(selector,sort,parameters.limit,parameters.lang)
  }

}


class TagsMongoService(db: Future[DB]) extends MongoCRUDService[models.Tag, String](db) with TagsService {
  
  override def collection(implicit ec: ExecutionContext): Future[JSONCollection] = db.map(_.collection("tags"))
  override def collectionCounters(implicit ec: ExecutionContext): Future[JSONCollection] = db.map(_.collection("counters"))
 
  override def newId(id: String)(implicit ec: ExecutionContext): Future[Option[Counter]] = {
    collectionCounters.flatMap(_.find(Json.obj(CounterIdentity.name -> id)).one[Counter]).map (
      // Este caso no esta tratado porque habria que crear la entidad y de momento se hara de forma manual
      _.fold(Option(Counter(Some(""),BigDecimal("1"),Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))))
      (entity => Option(entity.copy(value = entity.value.+(1))))
    )
  }

  override def translateEntity(entity: Future[Option[models.Tag]], lang: String)(implicit ec: ExecutionContext): Future[Option[models.Tag]] = {
    entity.map(innerEntity => {
        if(innerEntity.isDefined){
          Option(innerEntity.get.translate(innerEntity.get,lang))
        }else{
          innerEntity
        }
    })
  }

  override def translateEntities(entities: Future[Traversable[models.Tag]], lang: String)(implicit ec: ExecutionContext): Future[Traversable[models.Tag]] = {
    entities.map(prods => prods.map(pro => pro.translate(pro,lang)))
  }

}