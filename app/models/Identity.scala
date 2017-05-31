package models

/**
 * Type class providing identity manipulation methods
 */

sealed trait UtilsID {
  val collection : String
}

trait Identity[E, ID] extends UtilsID {

  val name: String
  val counter: String
  val collection: String
  def of(entity: E): ID
  def set(entity: E): E
  def clear(entity: E): E
  def next(entity: E): ID
  def setCounter(entity: E, newId: String): E
  def setAudit(entity: E): E
  def updateAudit(entity: E): E

}