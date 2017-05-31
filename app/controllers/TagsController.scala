package controllers

import play.api.cache.CacheApi
import services.TagsService
import services.TagsMongoService
import play.api.mvc._
import models.Find
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import models.Audit
import org.joda.time.DateTime
import utils.RequestUtils

import scala.concurrent.Future

class TagsController(service: TagsService)(cache: CacheApi) extends CRUDController[models.Tag, String](service)(cache) {

  def find = Action.async { implicit request =>

      val parameters = RequestUtils.getReqParams

      service.search(parameters)
       		.map(
            entities =>
         		  Ok(Json.toJson(models.RestResult[models.Tag](entities.toSeq,Seq(),models.Status.OK)))
      )
  	}
  
  def findByParent(idParent: String) = Action.async { implicit request =>

      val parameters = RequestUtils.getReqParams

      service.searchByParent(idParent,parameters)
       		.map(
         			entity => {
         		  //val a = models.Tag(Some(""),"",Some(Map(""->"")),Some(""),Some(Seq("")),Some(Audit(Some(false),Some(new DateTime),Some(new DateTime))))
         		  Ok(Json.toJson(models.RestResult[models.Tag](entity.toSeq,Seq(),models.Status.OK)))
         		})
  }

  def childs(idParent: String) = Action.async { implicit request =>
    val parameters = RequestUtils.getReqParams

    service.childsOf(idParent, parameters)
      .map(
        entities =>
          Ok(Json.toJson(models.RestResult[models.Tag](entities.toSeq,Seq(),models.Status.OK)))
      )
  }

}