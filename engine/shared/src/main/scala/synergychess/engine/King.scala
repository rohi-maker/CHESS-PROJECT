package synergychess.engine

import scala.collection.mutable.ArrayBuffer

class King(override val color: String, override val basePos: Point) extends Piece(color, basePos) {
  override val value = 100
  override val name = "king"

  val rank: String = if (basePos.toString == "F1" || basePos.toString == "F12") "back" else "inner"

  def this(another: King) {
    this(another.color, another.basePos)
  }

  override def squaresAttacking(board: Board, from: String): ArrayBuffer[String] = {
    val valids = ArrayBuffer[String]()
    val possible = Array(Point(1, 0), Point(1, 1), Point(1, -1), Point(0, 1), Point(0, -1), Point(-1, 1), Point(-1, -1), Point(-1, 0))

    // CheckBoard Adjacent Squares
    for (i <- possible.indices) {
      var valid = false
      val pos = new Point(from)
      val pPos = possible(i)
      val newPos = Point(pPos.x + pos.x, pPos.y + pos.y)
      val pStr = newPos.toString()
      if (newPos.inBounds) {
        if (board.getSquare(pStr) != null) {
          if (board.getSquare(pStr).color != color) {
            valid = true
          } else if (board.getSquare(pStr).color == color) {
            valid = false
          }
        }
        if (board.getSquare(pStr) == null) valid = true
        if (valid) valids.append(pStr)
      }
    }

    valids
  }

  override def senString: String = {
    if (color == "white") "K" else "k"
  }

  def isBackRankCastle(moveData: MoveData): Boolean = {
    val board = moveData.board
    val from = moveData.from
    val to = moveData.to

    val castling = moveData.castling

    // Can't castle from check
    if (board.inCheck(color)) return false

    var validCastle = ArrayBuffer[String]()
    val sqs = ArrayBuffer((from, null), (to, this))

    // Checks if the king in question is the backRank King
    if (basePos.toString == "F1" || basePos.toString == "F12") return false
    val p1 = new Point(from)
    val p2 = new Point(to)

    // If the king is moving horizontally
    if (p1.y != p2.y) return false
    val dir = if ((p1.x - p2.x) > 0) 0 else 1

    // Whether castling is valid in the direction chosen
    if (!castling.status(basePos.toString)(dir)) return false

    if (Math.abs(p1.x - p2.x) == 1) {  // Single jump can be castle or can be normal, NB is queried with an alert

      // OPS analysis - defines range of squares in a direction either side of the king, +ve is up alphabetically
      val dir = if (p1.x < p2.x) 6 else -5

      // Checks castling status either side for the king basePos, NB [0] is kingside boolean, [1 is QS boolean]
      // See castling.js status
      val kingSide = castling.status(from)(0)
      val queenSide = castling.status(from)(1)

      // Filters incorrect combinations
      if (dir == 6 && !queenSide) return false
      if (dir == -5 && !kingSide) return false

      // This defines the end point of the castling options for the king
      val castleSquare = Point(dir + p1.x, p1.y)  // Remember p1 is "from" ie King basePsition

      // This TBC - I think it steps all points between the kings start and the castleSquare end point
      validCastle = new Point(from).pointsBetween(castleSquare.toString)

      // Removes the last element of the array - i.e. where the king *lands* so not available for the rook to land on
      validCastle.remove(validCastle.length - 1)

      // Check if squares in between contain anything other than an empty square...
      return !validCastle.exists(_ != null)
    } else if (Math.abs(p1.x - p2.x) >= 2) {// is more than a single step castling move
      validCastle = new Point(to).pointsBetween(from)
      return true
    }
    false
  }

  override def validMoves(moveData: MoveData): ArrayBuffer[String] = {
    val board = moveData.board
    val from = moveData.from
    val castling = moveData.castling

    val valids = squaresAttacking(board, from)


    if (castling != null && castling.status.contains(basePos.toString)) {
      if ((castling.status(basePos.toString)(0) || castling.status(basePos.toString)(1)) && !board.inCheck(color)) {
        if (rank == "inner") {
          def innerCastleCheck(inc: Int) {
            val pos = new Point(from)

            pos.x += inc
            val p1str = pos.toString

            pos.x += inc
            val p2str = pos.toString

            pos.x += inc
            val p3str = pos.toString

            var invalid = false
            if ((p3str == "D3" || p3str == "D10") && board.getSquare(p3str) != null) invalid = true

            if (board.getSquare(p1str) != null || board.getSquare(p2str) != null || invalid) {}
            else if (!(board.underAttack(color, p1str) || board.underAttack(color, p2str))) {
              valids.append(p2str)
            }
          }

          if (castling.status(basePos.toString)(0)) innerCastleCheck(-1)
          if (castling.status(basePos.toString)(1)) innerCastleCheck(1)
        } else {
          val targets = if (basePos.toString == "F1") ArrayBuffer("A1", "L1") else ArrayBuffer("A12", "L12")
          if (!castling.status(basePos.toString)(0)) targets.remove(0, 1)
          if (!castling.status(basePos.toString)(1)) targets.remove(1, 1)

          def outerCastleCheck(target: String) {
            val points = basePos.pointsBetween(target)

            var pointsBtwn = points.clone()

            var nothingBetween = true
            for (i <- points.indices.dropRight(1)) {
              if (board.getSquare(points(i)) != null) {
                nothingBetween = false
              }
            }
            if (!nothingBetween) return

            for (i <- points.indices) {
              if (board.underAttack(color, points(i)) || board.getSquare(points(i)) != null && !targets.contains(points(i))) {
                pointsBtwn = points.dropRight(points.length - i)
                valids.appendAll(pointsBtwn)
                return
              }
            }
            valids.appendAll(pointsBtwn)
          }

          for (i <- targets.indices) {
            outerCastleCheck(targets(i))
          }
        }
      }
    }
    moveData.moveList = valids.distinct
    filterInvalidMoves(moveData)
  }

  override def move(moveData: MoveData): MoveResult = {
    val result = MoveResult()
    result.sq = ArrayBuffer[(String, Piece)]()
    result.from = moveData.from
    result.to = moveData.to
    result.isCapturing = moveData.board.getSquare(moveData.to) != null

    val p1 = new Point(moveData.from)
    val p2 = new Point(moveData.to)
    var castleSquare = new Point()
    var inBetween = new Point()
    if (moveData.rookPlacement != "") {
      val dir = if (p1.x < p2.x) 6 else -5
      castleSquare = Point(dir + p1.x, p1.y)
      val king = new King(moveData.board.getSquare(moveData.from).asInstanceOf[King])
      val rook = new Rook(moveData.board.getSquare(castleSquare).asInstanceOf[Rook])

      result.sq.append((moveData.from, null))
      result.sq.append((castleSquare.toString(), null))
      result.sq.append((moveData.to, king))
      result.sq.append((moveData.rookPlacement, rook))
      result.rookPlacement = moveData.rookPlacement
      return result
    }

    def innerCastle() {
      val dir = if (p1.x < p2.x) 3 else -4
      inBetween = Point((p1.x + p2.x) / 2, p2.y)
      castleSquare = Point(dir + p1.x, p1.y)
      result.sq.append((castleSquare.toString, null))
      result.sq.append((inBetween.toString, moveData.board.getSquare(castleSquare)))
      result.sq.append((moveData.to, this))
      result.sq.append((moveData.from, null))
      result.castle = "inner"
    }

    if (rank == "inner" && Math.abs(p1.x - p2.x) == 2) {
      innerCastle()
    } else {
      result.sq = ArrayBuffer((moveData.from, null), (moveData.to, this))
    }
    result
  }
}
