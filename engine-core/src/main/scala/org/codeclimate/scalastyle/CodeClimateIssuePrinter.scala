package org.codeclimate.scalastyle

import java.io.PrintStream

import com.typesafe.config.ConfigFactory
import io.circe.Printer
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalastyle.{FileSpec, Message, MessageHelper, StyleError}

import scala.collection.JavaConverters._

class CodeClimateIssuePrinter(ps: PrintStream) {
  private val codePrefix = "/code/"
  private val printer = Printer.noSpaces.copy(dropNullKeys = true)

  private val messageHelper = new MessageHelper(ConfigFactory.load())

  def printIssue[T <: FileSpec](msg: Message[T]): Unit = msg match {
    case se: StyleError[FileSpec] =>
      val errPosition = Position(se.lineNumber.getOrElse(0), se.column.getOrElse(0))
      val filePath = Option(se.fileSpec.name)
        .filter(_.startsWith(codePrefix))
        .map(_.substring(codePrefix.length))
        .getOrElse(se.fileSpec.name)

      val location = Location(path = filePath, positions = LinePosition(
        errPosition, errPosition
      ))
      val msg: String = messageHelper.message(se.key, se.args)
      val issue = Issue(location = location,
        description = String.format(msg, se.args.asJava),
        check_name = Some(se.clazz.getName),
        categories = Seq("Style"),
        severity = Some("major")
      )
      val jsonStr = printer.pretty(issue.asJson)
      ps.print(jsonStr)
      ps.print("\0")
    case _ => // ignore
  }
}
