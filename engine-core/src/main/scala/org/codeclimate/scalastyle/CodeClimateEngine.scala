package org.codeclimate.scalastyle

import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files

import io.circe.parser.parse

import scala.collection.JavaConverters._
import scala.util.Try

object CodeClimateEngine extends App {
  case class ProgramArgs(config_file_path: String, workspace_path: String)

  val argsParser = new scopt.OptionParser[ProgramArgs]("scopt") {
    opt[String]("config_file_path").action { case (path, conf) =>
      conf.copy(config_file_path = path)
    }

    opt[String]("workspace_path").action { case (path, conf) =>
      conf.copy(workspace_path = path)
    }
  }

  val defaultStyleConfigurationPath = "/usr/src/app/scalastyle_config.xml"

  argsParser.parse(args, ProgramArgs("/config.json", "/code")) match {
    case Some(programArgs) =>
      val configFile = new File(programArgs.config_file_path)
      val configJson = Try {
        Files.readAllLines(configFile.toPath, Charset.defaultCharset()).asScala.toSeq.mkString("\n")
      }.toEither

      val providedConfig = configJson.right.flatMap(parse).right
        .map(config => config.hcursor.downField("config"))

      val includePaths = providedConfig.right.flatMap(_.downField("include_paths").as[Seq[String]]).toOption.getOrElse(Seq.empty)
      val excludePaths = providedConfig.right.flatMap(_.downField("exclude_paths").as[Seq[String]]).toOption.getOrElse(Seq.empty)
      val configPath = providedConfig.right.flatMap(_.downField("config").as[String]).toOption.getOrElse(defaultStyleConfigurationPath)

      val config = ScalastyleCodeClimateConfiguration(configPath, includePaths, excludePaths)

      val ccPrinter = new CodeClimateIssuePrinter(programArgs.workspace_path, Console.out)

      ScalaStyleRunner.runCheckstyle(programArgs.workspace_path, config) foreach ccPrinter.printIssue
    case None => // it will print the error
  }
}
