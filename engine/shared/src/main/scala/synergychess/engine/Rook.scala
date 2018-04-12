package synergychess.engine

import scala.collection.mutable.ArrayBuffer

class Rook(override val color: String, override val basePos: Point) extends Piece(color, basePos) {
  override val value = 5
  override val name = "rook"

  def this(another: Rook) {
    this(another.color, another.basePos)
  }

  override def squaresAttacking(board: Board, from: String): ArrayBuffer[String] = {
    val pos = new Point(from)
    orthogonal(board, pos)
  }

  override def senString: String = {
    if (color == "white") "R" else "r"
  }

  override def validMoves(moveInfo: MoveData): ArrayBuffer[String] = {
    moveInfo.moveList = squaresAttacking(moveInfo.board, moveInfo.from)
    filterInvalidMoves(moveInfo)
  }
}
