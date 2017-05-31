package models

import org.joda.time.DateTime

trait Identifiable {
  def _id: Option[String]
}

trait Auditable {
  def audit: Option[Audit] //= Some(Audit(Some(false),Some(new DateTime()),Some(new DateTime())))
  def updateMoment : DateTime = audit.get.updateTS.getOrElse(DateTime.parse("20000101"))
  def areEquals(d2 : GenericEntity): Boolean = updateMoment.isEqual(d2.updateMoment)
}

trait MultiLang {
  def name: String
  def loc_names: Option[Map[String,String]]

  /*
  def copy(x: String): E

  def translate(entity : E, lang: String): E = {
    var newName : String = entity.asInstanceOf[MultiLang[E]].name
    if(entity.asInstanceOf[MultiLang[E]].loc_names.isDefined && entity.asInstanceOf[MultiLang[E]].loc_names.get.exists(_._1 == (lang))){
      newName = entity.asInstanceOf[MultiLang[E]].loc_names.get(lang)
    }
    var res : MultiLang[E] = entity.asInstanceOf[MultiLang[E]].clone.asInstanceOf[MultiLang[E]]
    res.name = newName
    res.asInstanceOf[E]

    entity.asInstanceOf[MultiLang[E]].copy(name = newName)


    /*
    res.asInstanceOf[MultiLang].name = newName
    res.asInstanceOf[M]*/


  }

/*
  var newName : String = entity.name
  if(entity.loc_names.isDefined && entity.loc_names.get.exists(_._1 == (lang))){
    newName = entity.loc_names.get(lang)


  }
  entity.copy(name = newName)*/

*/
}

trait GenericEntity extends Identifiable with Auditable