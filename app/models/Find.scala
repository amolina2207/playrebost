package models

case class Find (
    var search: Option[String],
    var sort: Option[String],
    var sort_dir: Int,
    var starting: Int,
    var limit: Int,
    var lang: String,
    var tags: Seq[String]
)