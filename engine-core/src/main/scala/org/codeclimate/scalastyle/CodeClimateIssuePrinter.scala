package org.codeclimate.scalastyle

import java.io.{File, PrintStream}

import com.typesafe.config.ConfigFactory
import io.circe.Printer
import io.circe.generic.auto._
import io.circe.syntax._
import org.scalastyle.{FileSpec, Message, MessageHelper, StyleError}

import scala.collection.JavaConverters._

class CodeClimateIssuePrinter(workspacePath: String, ps: PrintStream) {
  private val basePath = new File(workspacePath).toPath
  private val printer = Printer.noSpaces.copy(dropNullKeys = true)

  private val messageHelper = new MessageHelper(ConfigFactory.load())

  def printIssue[T <: FileSpec](msg: Message[T]): Unit = msg match {
    case se: StyleError[FileSpec] =>
      val errPosition = Position(se.lineNumber.getOrElse(0), se.column.getOrElse(0))
      val filePath = Option(se.fileSpec.name)
        .map(pathname => basePath.relativize(new File(pathname).toPath))
        .map(_.toString)
        .getOrElse(se.fileSpec.name)

      val location = Location(path = filePath, positions = LinePosition(
        errPosition, errPosition
      ))
      val msg: String = se.customMessage.orElse {
        Some(messageHelper.message(se.key, se.args))
      }.getOrElse("Error message not provided")
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
