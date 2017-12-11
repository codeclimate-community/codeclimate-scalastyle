package apackage

class TestFile {
  val MyVariable = ""

  def foobar(s: String) = {
    var a = 1
    lazy var b = a
    a = 2
    b
  }
}