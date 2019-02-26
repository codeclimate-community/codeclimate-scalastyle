package org.codeclimate.scalastyle

import java.io.{BufferedOutputStream, PrintStream}

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import org.scalastyle.file.FileTabChecker
import org.scalastyle.{RealFileSpec, StyleError, _}
import org.scalatest.{FunSuite, Matchers}

class CodeClimateIssuePrinterTest extends FunSuite with Matchers {

  private val fileSpec = new RealFileSpec("src/main/scala/org/codeclimate/scalastyle/CodeClimateIssuePrinter.scala", None)

  test("testPrintIssue") {
    val out = new ByteOutputStream()
    val err = new ByteOutputStream()
    val outPrinter = toPrintStream(out)
    val errPrinter = toPrintStream(err)
    val printer = new CodeClimateIssuePrinter(".", outPrinter, errPrinter)

    printer.printIssue(new StyleError[RealFileSpec](fileSpec, classOf[FileTabChecker],
      new FileTabChecker().errorKey, ErrorLevel, List.empty[String], Some(0)))

    outPrinter.flush()
    errPrinter.flush()

    out.size() should not be 0
    err.size() should be(0)
  }

  test("testPrintIssue bad format") {
    val out = new ByteOutputStream()
    val err = new ByteOutputStream()
    val outPrinter = toPrintStream(out)
    val errPrinter = toPrintStream(err)
    val printer = new CodeClimateIssuePrinter(".", outPrinter, errPrinter)

    val errKey = new FileTabChecker().errorKey
    printer.printIssue(new StyleError[RealFileSpec](fileSpec, classOf[FileTabChecker],
      errKey, ErrorLevel, List.empty[String], Some(0), customMessage = Some(
        "This is badly formatted custom message %'*'"
      )))

    outPrinter.flush()
    errPrinter.flush()

    out.size() should be(0)
    err.size() should not be 0
    err.toString should include(s"Invalid message format for key=$errKey")
  }

  private def toPrintStream(out: ByteOutputStream): PrintStream = {
    new PrintStream(new BufferedOutputStream(out))
  }
}
