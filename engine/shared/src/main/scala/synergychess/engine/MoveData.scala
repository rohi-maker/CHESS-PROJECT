package synergychess.engine

import scala.collection.mutable.ArrayBuffer

case class MoveData (
  var board: Board,
  var enPassant: String,
  var from: String,
  var to: String,
  var moveList: ArrayBuffer[String],
  var castling: Castling,
  var rookPlacement: String,
  var promotionData: PromotionData
) {
  def this() {
    this(
      new Board(),
      "",
      "",
      "",
      ArrayBuffer[String](),
      new Castling(),
      "",
      null
    )
  }

  def this(another: MoveData) {
    this(
      another.board,
      another.enPassant,
      another.from,
      another.to,
      another.moveList,
      another.castling,
      another.rookPlacement,
      another.promotionData
    )
  }
}
