package synergychess.engine

import scala.collection.mutable.ArrayBuffer

case class MoveResult(
   var sq: ArrayBuffer[(String, Piece)] = new ArrayBuffer[(String, Piece)](2),
   var enPassant: String = "",
   var from: String = "",
   var to: String = "",
   var rookPlacement: String = "",
   var pawnPromotion: Boolean = false,
   var isCapturing: Boolean = false,
   var castle: String = "",

   var mInfo: MoveData = null,
   var mateData: MateData = null,
   var completed: Boolean = false
) {
  def toNotation(game: Game): Notation = {
    val doubleThreat =
      game.board.checkDoubleThreat(sq(1)._2.name, to, from, game.board, game.negateTurn(game.teamToMove))

    val piece = if (pawnPromotion) "pawn" else sq(1)._2.name

    val notation = Notation(
      piece = piece,
      moveFrom = from,
      moveTo = to,
      isDoubleThreat = doubleThreat.isDoubleThreat,
      rankNeeded = doubleThreat.rankNeeded,
      fileNeeded = doubleThreat.fileNeeded,
      isInCheck = game.board.inCheck(game.teamToMove),
      isCheckmate = mateData.checkMate,
      isDoubleCheckMate = mateData.trueCheckMate,
      isPromotion = pawnPromotion,
      promoPiece = if (pawnPromotion) sq(1)._2.name else "",
      isTaken = isCapturing,
      enPassant = enPassant != "" && enPassant != null && enPassant == to
    )

    notation.castleWhichWay(mInfo)

    notation
  }
}
