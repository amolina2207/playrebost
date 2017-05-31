package controllers

import models._
import services._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._

import play.api.cache._
import utils.RequestUtils

class ProductsController(service: ProductsService)(cache: CacheApi) extends CRUDController[Product, String](service)(cache) {

  def find = Action.async { implicit request =>

    println("helloooo")

    val parameters = RequestUtils.getReqParams

    service.search(parameters)
      .map(
        entity => {
          Ok(Json.toJson(models.RestResult[Product](entity.toSeq, Seq(), models.Status.OK)))
        }
      )
  }
}