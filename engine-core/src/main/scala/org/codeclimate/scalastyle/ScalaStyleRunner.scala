package org.codeclimate.scalastyle

import java.io.File

import org.scalastyle._

/**
  * Computes files and run ScalastyleChecker against them.
  */
private object ScalaStyleRunner {
  def runCheckstyle(workspacePath: String, ccConfig: ScalastyleCodeClimateConfiguration): Seq[Message[FileSpec]] = {
    val paths = if (ccConfig.include_paths.isEmpty) {
      Seq(workspacePath)
    } else {
      ccConfig.include_paths.map(include => s"$workspacePath/$include")
    }
    val files = Directory.getFiles(None, paths.map(new File(_)), excludedFiles = ccConfig.exclude_paths)

    val scalastyleConfig = ScalastyleConfiguration.readFromXml(ccConfig.config)
    new ScalastyleChecker(None).checkFiles(scalastyleConfig, files)
  }
}
