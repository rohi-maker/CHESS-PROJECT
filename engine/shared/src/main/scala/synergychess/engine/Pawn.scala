package synergychess.engine

import scala.collection.mutable.ArrayBuffer

class Pawn(override val color: String, override val basePos: Point) extends Piece(color, basePos) {
  override val value = 1
  override def name: String = "pawn"

  var doubleJump = true

  def this(another: Pawn) {
    this(another.color, another.basePos)
  }

  def this(color: String, basePos: Point, doubleJump: Boolean) {
    this(color, basePos)
    this.doubleJump = doubleJump
  }

  override def squaresAttacking(board: Board, from: String): ArrayBuffer[String] = {
    val valids = ArrayBuffer[String]()

    val pos = new Point(from)
    val x = pos.x
    val y = pos.y

    val cc = if (color == "white") 1 else -1

    val p1 = Point(x - 1, y + cc)
    val p2 = Point(x + 1, y + cc)

    if (p1.inBounds && board.getSquare(p1) != null) valids.append(p1.toString)
    if (p2.inBounds && board.getSquare(p2) != null) valids.append(p2.toString)

    valids
  }

  override def senString: String = {
    val pStr = if (doubleJump) "p" else "i"
    if (color == "white") pStr.toUpperCase() else pStr
  }

  override def validMoves(thisMoveInfo: MoveData): ArrayBuffer[String] = {
    val moveInfo = new MoveData(thisMoveInfo)
    val board = moveInfo.board
    val enPassant = moveInfo.enPassant
    val moves = squaresAttacking(board, moveInfo.from)
    val pos = new Point(moveInfo.from)

    val x = pos.x
    val y = pos.y
    val cc = if (color == "white") 1 else -1
    val singleJump = Point(x, y + cc)

    def validCapture(pos: String): Boolean = {
      board.getSquare(pos) != null && board.getSquare(pos).color != color
    }

    for (i <- moves.length - 1 to 0 by -1) {
      if (!validCapture(moves(i))) moves.remove(i, 1)
    }

    if (doubleJump) {
      val nextPoint = Point(x, y + (if (color == "white") 2 else -2))
      if (nextPoint.inBounds) {
        val pointStr = nextPoint.toString
        val inBetween = Point(x, y + cc).toString
        if (board.getSquare(pointStr) == null && board.getSquare(inBetween) == null)  moves.append(pointStr)
      }
    }

    if (singleJump.inBounds && board.getSquare(singleJump) == null) {
      moves.append(singleJump.toString)
    }

    if (enPassant != "" && enPassant != null) {
      val ePoint = new Point(enPassant)
      val xDif = Math.abs(pos.x - ePoint.x)
      val yDif = pos.y - ePoint.y
      if (xDif == 1 && yDif == -1 * cc) {
        moves.append(enPassant)
      }
    }

    moveInfo.moveList = moves

    filterInvalidMoves(moveInfo)
  }

  def isPromotion(from: String, to: String): Boolean = {
    val endRank = if (color == "white") 12 else 1
    val p2 = new Point(to)
    p2.y == endRank
  }

  override def move(moveInfo: MoveData): MoveResult = {
    val result = new MoveResult()
    result.sq = ArrayBuffer((moveInfo.from, null), (moveInfo.to, this))
    result.enPassant = ""
    result.from = moveInfo.from
    result.to = moveInfo.to

    val p1 = new Point(moveInfo.from)
    val p2 = new Point(moveInfo.to)

    if (Math.abs(p1.y - p2.y) == 2) {
      val x = p1.x
      result.enPassant = Point(x, (p1.y + p2.y) / 2).toString
    }

    if (moveInfo.to == moveInfo.enPassant) {
      val p3 = new Point(moveInfo.enPassant)
      p3.y += (if (color == "white") -1 else 1)
      result.sq.append((p3.toString, null))
    }

    if (moveInfo.promotionData != null) {
      val newPiece = PieceFactory.newPiece(moveInfo.promotionData.name, color, basePos)
      moveInfo.board.setSquare(basePos, newPiece)
      if (moveInfo.promotionData.kingPlacement != null && moveInfo.promotionData.kingPlacement != "") {
        result.sq = ArrayBuffer((moveInfo.from, null), (moveInfo.to, null), (moveInfo.promotionData.kingPlacement, newPiece))
      } else {
        result.sq = ArrayBuffer((moveInfo.from, null), (moveInfo.to, newPiece))
      }
    }
    result
  }
}
