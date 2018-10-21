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
    var movedPieceName = ""
    if (sq.length < 4) {
      movedPieceName = sq(1)._2.name
    } else {
      movedPieceName = sq(2)._2.name
    }
    val doubleThreat =
      game.board.checkDoubleThreat(movedPieceName, to, from, game.board, game.negateTurn(game.teamToMove))

    val piece = if (pawnPromotion) "pawn" else movedPieceName
    val countKingInCheck = game.board.countKingInCheck(game.teamToMove)

    val notation = Notation(
      piece = piece,
      moveFrom = from,
      moveTo = to,
      isDoubleThreat = !pawnPromotion && doubleThreat.isDoubleThreat,
      rankNeeded = !pawnPromotion && doubleThreat.rankNeeded,
      fileNeeded = !pawnPromotion && doubleThreat.fileNeeded,
      isInCheck = countKingInCheck == 1,
      isInDoubleCheck = countKingInCheck == 2,
      isCheckmate = mateData.checkMate,
      isDoubleCheckMate = mateData.trueCheckMate,
      isPromotion = pawnPromotion,
      promoPiece = if (pawnPromotion) movedPieceName else "",
      isTaken = isCapturing,
      enPassant = enPassant != "" && enPassant != null && enPassant == to,
      kingRemoved = mInfo.kingChoice
    )

    notation.castleWhichWay(mInfo)

    notation
  }
}
