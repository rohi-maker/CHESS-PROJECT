package synergychess.engine

import scala.collection.mutable.ArrayBuffer

class Queen(override val color: String, override val basePos: Point) extends Piece(color, basePos) {
  override val value = 9
  override def name: String = "queen"

  def this(another: Queen) {
    this(another.color, another.basePos)
  }

  override def squaresAttacking(board: Board, from: String): ArrayBuffer[String] = {
    val pos = new Point(from)
    val result = orthogonal(board, pos)
    result.appendAll(diagonal(board, pos))
    result
  }

  override def senString: String = {
    if (color == "white") "Q" else "q"
  }

  override def validMoves(moveInfo: MoveData): ArrayBuffer[String] = {
    moveInfo.moveList = squaresAttacking(moveInfo.board, moveInfo.from)
    filterInvalidMoves(moveInfo)
  }
}
