package synergychess.engine

import scala.collection.mutable.ArrayBuffer

case class PromotionData(
  var safeSqs: ArrayBuffer[String],
  var validPieces: ArrayBuffer[String],
  // PromoSq is square landed on for promotion but not necessarily promoted to. i.e king may be placed elsewhere
  var kingPlacement: String,
  var name: String
) {
  def this() {
    this(
      ArrayBuffer[String](),
      ArrayBuffer[String](),
      "",
      ""
    )
  }
}
