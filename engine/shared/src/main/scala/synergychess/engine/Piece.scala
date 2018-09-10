package synergychess.engine

import scala.collection.mutable.ArrayBuffer

class Piece(val color: String, val basePos: Point) {
  val value: Int = 0

  def this(another: Piece) {
    this(another.color, another.basePos)
  }

  def name: String = ""

  def senString: String = ""

  def validMoves(moveData: MoveData): ArrayBuffer[String] = null

  def squaresAttacking(board: Board, from: String): ArrayBuffer[String] = null

  def step(board: Board, pos: Point, xOff: Int, yOff: Int): ArrayBuffer[String] = {
    // Given a starting position and a direction
    // Step in that direction until you reach something i.e. edge of board/piece etc
    // Return list of empty squares in between that point
    val result = ArrayBuffer[String]()
    val tempP = Point(pos.x + xOff, pos.y + yOff)

    var broke = false
    while (tempP.inBounds && !broke) {
      val str = tempP.toString
      if (board.getSquare(str) == null) {
        result.append(str)
      } else {
        if (board.getSquare(str).color != color) {
          result.append(str)
        }
        broke = true
      }

      tempP.x += xOff
      tempP.y += yOff
    }

    result
  }
  def step(board: Board, pos: String, xOff: Int, yOff: Int): ArrayBuffer[String] = {
    step(board, new Point(pos), xOff, yOff)
  }

  // Modified version of piece.step above to get any pieces encountered, not just opposite team
  // Used for doubleThreat calculations
  def ownTeamStep(board: Board, pos: Point, xOff: Int, yOff: Int): ArrayBuffer[String] = {
    // Given a starting position and a direction
    // Step in that direction until you reach something i.e. edge of board/piece etc
    // Return list of empty squares in between that point
    val result = ArrayBuffer[String]()
    val tempP = Point(pos.x + xOff, pos.y + yOff)

    var broke = false
    while (tempP.inBounds && !broke) {
      val str = tempP.toString()
      result.append(str)

      if (board.getSquare(str) != null) {
        broke = true
      }

      tempP.x += xOff
      tempP.y += yOff
    }

    result
  }
  def ownTeamStep(board: Board, pos: String, xOff: Int, yOff: Int): ArrayBuffer[String] = {
    ownTeamStep(board, new Point(pos), xOff, yOff)
  }

  def orthogonal(board: Board, point: Point): ArrayBuffer[String] = {
    val validSqs = ArrayBuffer[String]()
    validSqs.appendAll(step(board, point, 1, 0))
    validSqs.appendAll(step(board, point, -1, 0))
    validSqs.appendAll(step(board, point, 0, 1))
    validSqs.appendAll(step(board, point, 0, -1))

    validSqs
  }
  def orthogonal(board: Board, pos: String): ArrayBuffer[String] = {
    orthogonal(board, new Point(pos))
  }

  def diagonal(board: Board, point: Point): ArrayBuffer[String] = {
    val validSqs = ArrayBuffer[String]()
    validSqs.appendAll(step(board, point, -1, -1))
    validSqs.appendAll(step(board, point, -1, 1))
    validSqs.appendAll(step(board, point, 1, 1))
    validSqs.appendAll(step(board, point, 1, -1))
    validSqs
  }
  def diagonal(board: Board, pos: String): ArrayBuffer[String] = {
    diagonal(board, new Point(pos))
  }

  def move (moveData: MoveData): MoveResult = {
    val isCapturing = moveData.board.getSquare(moveData.to) != null
    MoveResult(
      sq = ArrayBuffer(Tuple2(moveData.from, null), Tuple2(moveData.to, this)),
      from = moveData.from,
      to = moveData.to,
      isCapturing = isCapturing
    )
  }

  def filterInvalidMoves(moveData: MoveData): ArrayBuffer[String] = {
    val board = moveData.board
    val validMoves = moveData.moveList

    val testMove = MoveData()
    testMove.board = moveData.board
    testMove.from = moveData.from

    // Given a set of validMoves filter the ones that don't end up in a checked Positions
    val color = board.getSquare(moveData.from).color

    var i = 0
    while (i < validMoves.length) {
      testMove.to = validMoves(i)
      val p = board.getSquare(testMove.from)
      val result = p.move(testMove)
      val sqsChanged = result.sq
      val oldMoves = tryMove(board, sqsChanged)
      if (board.inCheck(color)) {
        validMoves.remove(i, 1)
        i -= 1
      }
      revert(oldMoves)
      i += 1
    }

    def tryMove(board: Board, sqsChanged: ArrayBuffer[(String, Piece)]): ArrayBuffer[(String, Piece)] = {
      val oldSquares = ArrayBuffer[(String, Piece)]()
      for (i <- sqsChanged.indices) {
        val pos = sqsChanged(i)._1
        val value = sqsChanged(i)._2
        oldSquares.append((pos, Piece.newPiece(board.getSquare(pos), pos)))
        board.setSquare(pos, value)
      }
      oldSquares
    }

    def revert(oldMoves: ArrayBuffer[(String, Piece)]) {
      for (i <- oldMoves.indices) {
        val pos = oldMoves(i)._1
        val value = oldMoves(i)._2
        board.setSquare(pos, value)
      }
    }

    validMoves
  }
}

object Piece {
  def newPiece(another: Piece, pos: String): Piece = {
    if (another != null) {
      val result = another match {
        case _: Rook => new Rook(another.asInstanceOf[Rook])
        case _: Knight => new Knight(another.asInstanceOf[Knight])
        case _: Bishop => new Bishop(another.asInstanceOf[Bishop])
        case _: King => new King(another.asInstanceOf[King])
        case _: Queen => new Queen(another.asInstanceOf[Queen])
        case _: Pawn => new Pawn(another.asInstanceOf[Pawn])
      }
      val newPos = new Point(pos)
      result.basePos.x = newPos.x
      result.basePos.y = newPos.y
      result
    } else null
  }
}
