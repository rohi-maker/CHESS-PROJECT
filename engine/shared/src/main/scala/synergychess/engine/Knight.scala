package synergychess.engine

import scala.collection.mutable.ArrayBuffer

class Knight(override val color: String, override val basePos: Point) extends Piece(color, basePos) {
  override val value = 3
  override def name: String = "knight"

  def this(another: Knight) {
    this(another.color, another.basePos)
  }

  override def squaresAttacking(board: Board, from: String): ArrayBuffer[String] = {
    val valids = ArrayBuffer[String]()
    val possible = Array(Point(-1, 2), Point(-1, -2), Point(1, 2), Point(1, -2), Point(-2, 1), Point(2, 1), Point(2, -1), Point(-2, -1))

    val pos = new Point(from)
    val x = pos.x
    val y = pos.y

    for (i <- possible.indices) {
      val sq = possible(i)
      val adj = Point(sq.x + x, sq.y + y)
      if (adj.inBounds && board.getSquare(adj) != null && board.getSquare(adj).color != color) {
        valids.append(adj.toString())
      }
    }

    valids
  }

  override def validMoves(moveInfo: MoveData): ArrayBuffer[String] = {
    val valids = ArrayBuffer[String]()
    val possible = Array(Point(-1, 2), Point(-1, -2), Point(1, 2), Point(1, -2), Point(-2, 1), Point(2, 1), Point(2, -1), Point(-2, -1))

    val pos = new Point(moveInfo.from)
    val x = pos.x
    val y = pos.y

    for (i <- possible.indices) {
      val sq = possible(i)
      val adj = Point(sq.x + x, sq.y + y)
      if (adj.inBounds && (moveInfo.board.getSquare(adj) == null || moveInfo.board.getSquare(adj).color != color)) {
        valids.append(adj.toString())
      }
    }

    moveInfo.moveList = valids
    filterInvalidMoves(moveInfo)
  }

  override def senString: String = {
    if (color == "white") "N" else "n"
  }
}
