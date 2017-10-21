package org.codeclimate.scalastyle

import java.io.File

import org.scalastyle._

/**
  * Computes files and run ScalastyleChecker against them.
  */
private object ScalaStyleRunner {
  def runCheckstyle(ccConfig: ScalastyleCodeClimateConfiguration): Seq[Message[FileSpec]] = {
    val files = Directory.getFiles(None, ccConfig.directories.map(new File(_)), excludedFiles = ccConfig.exclude_paths)

    val scalastyleConfig = ScalastyleConfiguration.readFromXml(ccConfig.config)
    new ScalastyleChecker(None).checkFiles(scalastyleConfig, files)
  }
}
