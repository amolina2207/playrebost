package utils

import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}

class DateUtils {
}

object DateUtils{

  def checkIfDatesAreEquals(oldDT: DateTime, newDT: DateTime)(implicit ec: ExecutionContext): Boolean = {
    (oldDT==newDT)
  }

}
