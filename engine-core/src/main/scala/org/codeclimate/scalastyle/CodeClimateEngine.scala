package org.codeclimate.scalastyle

import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files

import io.circe.generic.auto._
import io.circe.parser.parse

import scala.collection.JavaConverters._
import scala.util.Try

object CodeClimateEngine extends App {
  val defaultStyleConfigurationPath = "/usr/src/app/scalastyle_config.xml"

  val configFile = new File("/config.json")
  val configJson = Try {
    Files.readAllLines(configFile.toPath, Charset.defaultCharset()).asScala.toSeq.mkString("\n")
  }.toEither

  val providedConfig = configJson.right.flatMap(parse).right
    .map(config => config.hcursor.downField("config"))
    .flatMap(_.as[ScalastyleCodeClimateConfiguration])
  val config = providedConfig.right.toOption.getOrElse(
    ScalastyleCodeClimateConfiguration(defaultStyleConfigurationPath, directories = Seq("/code"))
  )

  val ccPrinter = new CodeClimateIssuePrinter(Console.out)

  ScalaStyleRunner.runCheckstyle(config) foreach ccPrinter.printIssue
}
