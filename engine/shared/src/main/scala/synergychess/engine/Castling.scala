package synergychess.engine

class Castling {
  // OPS what options are available, ie. Q: can I castle here?
  // Original code not OPS
  // King Position : kingSide value, queenSide Value
  var status: scala.collection.mutable.Map[String, Array[Boolean]] = scala.collection.mutable.Map(
    "F1"  -> Array(true, true),
    "G3"  -> Array(true, true),
    "G10" -> Array(true, true),
    "F12" -> Array(true, true)
  )

  def reset() {
    status = scala.collection.mutable.Map(
      "F1"  -> Array(true, true),
      "G3"  -> Array(true, true),
      "G10" -> Array(true, true),
      "F12" -> Array(true, true)
    )
  }

  // OPS analysis - tracks/manages the options remaining for castling
  def trigger(pos: String) {
    pos match {
      // White backRank
      case "A1" => status("F1")(0) = false
      case "F1" => status("F1") = Array(false, false)
      case "L1" => status("F1" )(1) = false

      // White innerRank
      case "C3" => status("G3")(0) = false
      case "G3" => status("G3") = Array(false, false)
      case "J3" => status("G3")(1) = false

      // Black backRank
      case "A12" => status("F12")(0) = false
      case "F12" => status("F12") = Array(false, false)
      case "L12" => status("F12")(1) = false

      // Black InnerRank
      case "C10" => status("G10")(0) = false
      case "G10" => status("G10") = Array(false, false)
      case "J10" => status("G10")(1) = false

      case _ =>
    }
  }

  def configure(wConfig: String, bConfig: String) {
    if (wConfig == "-") {
      status("F1") = Array(false, false)
      status("F3") = Array(false, false)
    }
    if (bConfig == "-") {
      status("G10") = Array(false, false)
      status("F12") = Array(false, false)
    }
    else {
      if (wConfig.contains("K")) status("F1")(0) = true
      if (wConfig.contains("Q")) status("F1")(1) = true
      if (wConfig.contains("k")) status("G3")(0) = true
      if (wConfig.contains("q")) status("G3")(1) = true

      if (bConfig.contains("K")) status("F12")(0) = true
      if (bConfig.contains("Q")) status("F12")(1) = true
      if (bConfig.contains("k")) status("G10")(0) = true
      if (bConfig.contains("q")) status("G10")(1) = true
    }
  }

  def senString(): String = {
    val sen = StringBuilder.newBuilder

    if ((status("F1") sameElements  Array(false, false)) && (status("G3") sameElements Array(false, false))) {
      sen.append("-")
    } else {
      if (status("F1")(0)) sen.append("K")
      if (status("F1")(1)) sen.append("Q")
      if (status("G3")(0)) sen.append("k")
      if (status("G3")(1)) sen.append("q")
    }

    sen.append(" ")

    if ((status("F12") sameElements Array(false, false)) && (status("G10") sameElements Array(false, false))) {
      sen.append("-")
    } else {
      if (status("F12")(0)) sen.append("K")
      if (status("F12")(1)) sen.append("Q")
      if (status("G10")(0)) sen.append("k")
      if (status("G10")(1)) sen.append("q")
    }
    sen.result()
  }
}
