package controllers

import services.ProvidersMongoService
import models.Provider
import models.Product
import play.api.cache.CacheApi
import services.ProvidersService
import play.api.mvc.Action
import models.Find
import services.ProductsService
import play.api.libs.json.Json
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import utils.RequestUtils

class ProvidersController(service: ProvidersService)(productsService: ProductsService)(cache: CacheApi) extends CRUDController[Provider, String](service)(cache) {
 
  def findProductsByProvider(idProvider: String) = Action.async { implicit request =>

    val parameters = RequestUtils.getReqParams

    productsService.searchByProvider(idProvider,parameters)
     		.map(
       		entity => 
       		  Ok(Json.toJson(models.RestResult[Product](entity.toSeq,Seq(),models.Status.OK)))
    )
  }
}