package org.codeclimate.scalastyle

import org.scalastyle.StyleError
import org.scalatest.Matchers

class CodeClimateRunnerTest extends org.scalatest.FreeSpec with Matchers {
  val codeClimateConfiguration = ScalastyleCodeClimateConfiguration(
    config = "engine-core/src/test/resources/scalastyle_config.xml",
    directories = Seq("engine-core/src/test/resources")
  )

  "CodeClimateEngine" - {
    "should call sclacheck and produce style errors for both files in apackage" in {
      val msgs = ScalaStyleRunner.runCheckstyle(codeClimateConfiguration)

      msgs should not be empty

      val styleErrors = msgs.flatMap {
        case se: StyleError[_] => Seq(se)
        case _ => Seq.empty
      }

      styleErrors should have size 3 // pre-computed number of issues
    }

    "should ignore files specified in `exclude_paths`" in {
      val msgs = ScalaStyleRunner.runCheckstyle(codeClimateConfiguration.copy(exclude_paths = Seq("TestFileToIgnore")))

      val files = msgs.flatMap {
        case se: StyleError[_] => Seq(se.fileSpec.name)
        case _ => Seq.empty
      }

      files filter(_.contains("TestFileToIgnore")) shouldBe empty
    }
  }
}
