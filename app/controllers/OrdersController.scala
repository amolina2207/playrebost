package controllers

import models.{Order, Product, Provider}
import play.api.cache.CacheApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.Action
import services.{OrdersService, ProductsService, ProvidersService}
import utils.RequestUtils

class OrdersController(service: OrdersService)(cache: CacheApi) extends CRUDController[models.Order, String](service)(cache) {

  def find = Action.async { implicit request =>

    val parameters = RequestUtils.getReqParams

    service.search(parameters)
      .map(
        entity => {
          Ok(Json.toJson(models.RestResult[Order](entity.toSeq, Seq(), models.Status.OK)))
        }
      )
  }

}