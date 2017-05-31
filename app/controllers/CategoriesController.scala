package controllers

import play.api.cache.CacheApi
import models.Category
import services.CategoriesMongoService
import services.CategoriesService

class CategoriesController(service: CategoriesService)(cache: CacheApi) extends CRUDController[Category, String](service)(cache) {
  
}