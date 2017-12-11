package org.codeclimate

package object scalastyle {
  private[this] val DEFAULT_REMEDIAION_POINTS = 50000

  case class ScalastyleCodeClimateConfiguration(
    config: String,
    include_paths: Seq[String] = Seq.empty,
    exclude_paths: Seq[String] = Seq.empty
  )

  sealed trait IssueSchema extends Product with Serializable
  case class Position(line: Int, column: Int) extends IssueSchema
  case class LinePosition(begin: Position, end: Position) extends IssueSchema
  case class Location(path: String, positions: LinePosition) extends IssueSchema
  case class Issue(location: Location, description: String, check_name: Option[String] = None,
    severity: Option[String] = None, remediation_points: Option[Int] = Some(DEFAULT_REMEDIAION_POINTS),
    fingerprint: Option[String] = None, `type`: String = "issue", categories: Seq[String] = Seq.empty
  ) extends IssueSchema
}
