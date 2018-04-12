package synergychess.engine

import scala.collection.mutable.ArrayBuffer

class Bishop(override val color: String, override val basePos: Point) extends Piece(color, basePos) {
  override val value = 4
  override def name: String = "bishop"

  def this(another: Bishop) {
    this(another.color, another.basePos)
  }

  override def squaresAttacking(board: Board, from: String): ArrayBuffer[String] = diagonal(board, from)

  override def validMoves(moveInfo: MoveData): ArrayBuffer[String] = {
    val move = new MoveData(moveInfo)
    move.moveList = squaresAttacking(move.board, move.from)

    filterInvalidMoves(move)
  }

  override def senString: String = {
    if (color == "white") "B" else "b"
  }
}
