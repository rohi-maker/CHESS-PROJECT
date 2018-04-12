package synergychess.engine

import scala.collection.mutable.ArrayBuffer

case class MoveResult(
   var sq: ArrayBuffer[(String, Piece)],
   var enPassant: String,
   var from: String,
   var to: String,
   var rookPlacement: String,
   var pawnPromotion: Boolean,
   var castle: String,

   var mInfo: MoveData,
   var mateData: MateData,
   var completed: Boolean
) {
  def this() {
    this(
      new ArrayBuffer[(String, Piece)](2),
      "",
      "",
      "",
      "",
      false,
      "",
      null,
      null,
      false
    )
  }
}
