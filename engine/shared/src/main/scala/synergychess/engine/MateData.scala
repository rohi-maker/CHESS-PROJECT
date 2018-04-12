package synergychess.engine

import scala.collection.mutable.ArrayBuffer

case class MateData(
  var trueCheckMate: Boolean,
  var checkMate: Boolean,
  var staleMate: Boolean,
  var kingsDead: ArrayBuffer[String],
  var safeKings: ArrayBuffer[String],
  // Kings that can be removed
  var choices: ArrayBuffer[String]
) {
  def this() {
    this(false, false, false, ArrayBuffer[String](), ArrayBuffer[String](), ArrayBuffer[String]())
  }

  override def toString: String = {
    trueCheckMate.toString + "," +
      checkMate.toString + "," +
      staleMate.toString + "," +
      (if (choices.nonEmpty) choices.reduce((res, e) => res + " " + e) else "")
  }
}
