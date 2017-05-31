package models

import org.joda.time.DateTime

case class Audit(
  var delete: Option[Boolean],
  var createTS: Option[DateTime],
  var updateTS: Option[DateTime])
