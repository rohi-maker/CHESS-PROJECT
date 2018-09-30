package synergychess.engine

import scala.collection.mutable.ArrayBuffer

class Board {
  val gameBoard: scala.collection.mutable.Map[String, Piece] = scala.collection.mutable.Map[String, Piece]()

  def getSquare(pos: String): Piece = gameBoard(pos)
  def getSquare(pos: Point): Piece = gameBoard(pos.toString)

  def setSquare(pos: String, value: Piece) {
    gameBoard(pos) = value
  }
  def setSquare(pos: Point, value: Piece) {
    gameBoard(pos.toString) = value
  }

  def removeSquare(pos: String) {
    gameBoard(pos) = null
  }
  def removeSquare(pos: Point) {
    gameBoard(pos.toString) = null
  }

  def squaresAttackedBy: Map[String, scala.collection.mutable.Map[String, ArrayBuffer[String]]] = {
    // Return squares attacked by each team
    val white = scala.collection.mutable.Map[String, ArrayBuffer[String]]()
    val black = scala.collection.mutable.Map[String, ArrayBuffer[String]]()

    for (pos <- gameBoard.keys) {
      val v = getSquare(pos)

      if (v != null) {
        val color = v.color
        val moveData = MoveData()
        moveData.from = v.basePos.toString
        moveData.board = this
        val sqsAtkd = v.validMoves(moveData)

        for (i <- sqsAtkd.indices) {
          if (color == "white") {
            if (!white.contains(sqsAtkd(i))) white(sqsAtkd(i)) = ArrayBuffer[String]()
            white(sqsAtkd(i)).append(pos)
          } else if (color == "black") {
            if (!black.contains(sqsAtkd(i))) black(sqsAtkd(i)) = ArrayBuffer[String]()
            black(sqsAtkd(i)).append(pos)
          }
        }
      }
    }

    Map("white" -> white, "black" -> black)
  }

  def safeSquares(color: String): ArrayBuffer[String] = {
    // Return squares that are not under attack by enemy color
    val ap = attackedPositions(color)
    val safes = ArrayBuffer[String]()
    for (k <- gameBoard.keys) {
      val v = getSquare(k)
      if (!ap.contains(k) && v == null) {
        safes.append(k)
      }
    }
    safes
  }

  def underAttack(color: String, pos: String): Boolean = {
    // Given a specific position returns true if that position is being attacked
    val attacked = attackedPositions(color)
    attacked.indexOf(pos) > -1
  }

  def attackedPositions(color: String): ArrayBuffer[String] = {
    // Returns an array of all attacked positions by enemy color
    val attacked = ArrayBuffer[String]()
    for (pos <- gameBoard.keys) {
      val v = getSquare(pos)
      if (v != null && v.color != color) {
        attacked.appendAll(v.squaresAttacking(this, pos))
      }
    }
    attacked
  }

  def getKingPositions(color: String): Array[String] = {
    gameBoard.keys.filter(pos =>
      if (getSquare(pos) == null) false
      else if (getSquare(pos).isInstanceOf[King] && getSquare(pos).color == color) true
      else false
    ).toArray
  }

  def inCheck(color: String, kingPos: String): Boolean = {
    // Checks if color given is in check
    var inCheck = false
    val orthogs = Array(Array(1, 0), Array(-1, 0), Array(0, 1), Array(0, -1))
    val diags = Array(Array(1, 1), Array(-1, 1), Array(1, -1), Array(-1, -1))
    val pos = kingPos

    val king = getSquare(pos)
    val kPoint = new Point(pos)

    // ORTHOGANAL
    for (offset <- orthogs) {
      val cDir = king.step(this, new Point(pos), offset(0), offset(1))
      if (cDir.nonEmpty) {
        // target object found at end of steps function (orig code)
        val target = getSquare(cDir.last)
        cDir.remove(cDir.length - 1)
        if (target.isInstanceOf[Queen] || target.isInstanceOf[Rook]) {
          return true
        }
      }
    }

    // DIAGONAL
    for (offset <- diags) {
      var broke = false
      val cDir = king.step(this, new Point(pos), offset(0), offset(1))
      if (cDir.nonEmpty) {
        // Target object found at end of steps function (orig code)
        val target = getSquare(cDir(cDir.length - 1))
        val kingColor = king.color

        // If a pawn and diag one step away
        if (target.isInstanceOf[Pawn] && cDir.length == 1) {

          // OPS SOLUTION to "reverse check" issue
          // Filter by offset to only allow "pawn forward" diagonal threats
          // Only need to look at y direction (offset(1)) NB is from the KING's perspective

          // y-Axis offset value
          val threatDirection = offset(1)

          if (kingColor == "black" && threatDirection == 1) { // black king looking "up" at target pawn
            broke = true
          } else if (kingColor == "white" && threatDirection == -1) { // white king looking "down" at target pawn
            broke = true
          } else { // pawn is a threat
            return true
          }
        }

        // Check for Queen or Bishop
        if (!broke && (target.isInstanceOf[Queen] || target.isInstanceOf[Bishop])) {
          return true
        }
      }
    }

    // KNIGHT
    val knightSquares = Array(  // Squares *around the king* which if a knight was there, would result in check
      Point(-1, 2),
      Point(-1, -2),
      Point(1, 2),
      Point(1, -2),
      Point(-2, 1),
      Point(2, 1),
      Point(2, -1),
      Point(-2, -1)
    )

    for (nSq <- knightSquares) {
      val adj = Point(kPoint.x + nSq.x, kPoint.y + nSq.y)
      if (adj.inBounds) {
        val cPiece = getSquare(adj)
        if (cPiece != null) {
          inCheck = inCheck || (cPiece.color != color && cPiece.isInstanceOf[Knight])
        }
      }
    }

    if (inCheck) {
      return true
    }

    inCheck
  }

  def inCheck(color: String): Boolean = {
    val kings = this.getKingPositions(color)

    for (pos <- kings) {
      if (inCheck(color, pos)) return true
    }
    false
  }

  def countKingInCheck(color: String): Int = {
    val kings = this.getKingPositions(color)
    var count = 0

    for (pos <- kings) {
      if (inCheck(color, pos)) count += 1
    }
    count
  }

  def checkDoubleThreat(attackingPieceName: String, to: String, from: String, board: Board, color: String): Notation = {
    // Checks if a square is threatened by two of the same type pieces
    // So notation can represent the move correctly

    val orthogs = ArrayBuffer(Array(1, 0), Array(-1, 0), Array(0, 1), Array(0, -1))
    val diags = ArrayBuffer(Array(1, 1), Array(-1, 1), Array(1, -1), Array(-1, -1))

    val knightSquares = Array(  // Squares around the threatened square which if a knight was there, would result in threat
      Point(-1, 2),
      Point(-1, -2),
      Point(1, 2),
      Point(1, -2),
      Point(-2, 1),
      Point(2, 1),
      Point(2, -1),
      Point(-2, -1)  // Last nSq for adjacent Knight checking
    )

    val target = to
    val fromPoint = new Point(from)
    val toPoint = new Point(to)
    var foundPiece: Piece = null
    val notation: Notation = Notation()

    // Pawns - double threat disambiguating not needed as pawn can only attack one square or en passant

    //  KNIGHT double threats
    if (attackingPieceName == "knight") {
      // Check the target square for any other knights threatening it
      var isDoubleThreat = false

      for (nSq <- knightSquares) {
        val adj = Point(new Point(target).x + nSq.x, new Point(target).y + nSq.y)

        if (adj.inBounds) {
          val foundPiece = board.getSquare(adj)

          if (foundPiece != null && foundPiece.isInstanceOf[Knight] && foundPiece.color == color) {
            notation.isDoubleThreat = true

            // Compare file, if the same, then
            if (fromPoint.y == adj.y) {
              notation.rankNeeded = true
            }

            // Compare rank, if the same, then
            if (fromPoint.x == adj.x) {
              notation.fileNeeded = true
            }

            // If a double threat with no common rank or file => either rank or file is okay
            // => save it till the end of the loop to choose
            if (fromPoint.x != adj.x && fromPoint.y != adj.y) {
              isDoubleThreat = true
            }
          }
        }
      }

      // If is double threat but neither rank nor file is selected
      if (isDoubleThreat && !notation.rankNeeded && !notation.fileNeeded) {
        notation.fileNeeded = true
      }
    }


    // DIAGONALS - queens and bishops
    if (attackingPieceName == "queen" || attackingPieceName == "bishop") {
      // Check diagonals
      val diagPieces = ArrayBuffer[String]()

      // Inline diagonals case where the piece is moving away from another behind it
      // Filter out the direction the piece came *from* by removing that directional element in the diags array.
      // Refer val diags = ((1,1),(-1,1),(1,-1),(-1,-1))
      if (fromPoint.x < toPoint.x && fromPoint.y < toPoint.y) diags.remove(3,1)  // -1 -1 case
      if (fromPoint.x < toPoint.x && fromPoint.y > toPoint.y) diags.remove(1,1)  // -1 +1 case
      if (fromPoint.x > toPoint.x && fromPoint.y < toPoint.y) diags.remove(2,1)  // +1 -1 case
      if (fromPoint.x > toPoint.x && fromPoint.y > toPoint.y) diags.remove(0,1)  // +1 +1 case


      // For each direction have a look diagonally and record the last square found
      for (offset <- diags) {
        val diagSquares = board.getSquare(target).ownTeamStep(board, toPoint, offset(0), offset(1))
        if (diagSquares.nonEmpty) {
          // Add last square found may or may not be a piece (ie could be board edge empty square)
          val lastSquare = board.getSquare(diagSquares(diagSquares.length - 1))

          // Filter undefined (square against edge) and empty squares
          if (lastSquare != null) {
            diagPieces.append(diagSquares(diagSquares.length - 1))
          }
        }
      }

      // Check those squares for the same type of piece
      for (pos <- diagPieces) {
        val posPoint = new Point(pos)
        foundPiece = board.getSquare(pos)

        if (foundPiece.name == attackingPieceName && foundPiece.color == color) {
          notation.isDoubleThreat = true
          // If the found piece has the same rank as where the attacking piece is coming from
          // Tell notation to show the file
          if (fromPoint.y == posPoint.y) notation.fileNeeded = true
          if (fromPoint.x == posPoint.x) notation.rankNeeded = true//ditto file
          if (fromPoint.x != posPoint.x && fromPoint.y != posPoint.y) notation.fileNeeded = true
          return notation
        }
      }
    }

    // Check ORTHOGONALS

    if (attackingPieceName == "queen" || attackingPieceName == "rook") {
      val orthogPieces = ArrayBuffer[String]()

      // Inline orthogonal case where the piece is moving away from another behind it
      // Mask the direction the piece came *from* by splicing that directional element in the diags array
      // Before looking along those lines...
      // Refer val orthogs = ((1,0),(-1,0),(0,1),(0,-1))

      if (fromPoint.x < toPoint.x && fromPoint.y == toPoint.y) orthogs.remove(1,1)  // Moves to right
      if (fromPoint.x > toPoint.x && fromPoint.y == toPoint.y) orthogs.remove(0,1)  // Moves to left
      if (fromPoint.x == toPoint.x && fromPoint.y < toPoint.y) orthogs.remove(3,1)  // Moves up (rel to x,y scale)
      if (fromPoint.x == toPoint.x && fromPoint.y > toPoint.y) orthogs.remove(2,1)  // Moves down (rel to x,y scale)

      for (offset <- orthogs) {
        val orthogSquares = board.getSquare(target).ownTeamStep(board, toPoint, offset(0), offset(1))
        if (orthogSquares.nonEmpty) {
          val lastSquare = board.getSquare(orthogSquares(orthogSquares.length - 1))

          // Add last square found may or may not be a piece (ie could be board edge empty square)
          if (lastSquare != null) { // Filter empty and undefined squares
            orthogPieces.append(orthogSquares(orthogSquares.length - 1)) // Record the square
          }
        }
      }

      // Check those squares for the same type of piece
      for (pos <- orthogPieces) {
        val posPoint = new Point(pos)
        val foundPiece = board.getSquare(pos.toString)
        if (foundPiece.name == attackingPieceName && foundPiece.color == color) {
          notation.isDoubleThreat = true

          //NB pos is the *other found orthog piece* position

          // Inline moves
          // The moving rook and the found rook are on the same rank, file needed
          if (fromPoint.y == posPoint.y) notation.fileNeeded = true
          // The moving rook and the found rook are on the same file, rank needed
          if (fromPoint.x == posPoint.x) notation.rankNeeded = true

          // Intersecting moves
          // The rook is moving along a rank, and another rook is threatening the target square along it's file
          if (fromPoint.y != toPoint.y && toPoint.y == posPoint.y) notation.fileNeeded = true
          // The rook is moving along a file, and another rook is threatening the target square along it's rank
          if (fromPoint.x != toPoint.x && toPoint.x == posPoint.x) notation.rankNeeded = true

          return notation
        }
      }
    }

    // KING double threats
    if (attackingPieceName == "king" && !notation.isOuterKingCastle && !notation.isOuterQueenCastle) {

      val kingSquares = Array(
        Point(-1, -1),
        Point(-1, 0),
        Point(-1, 1),
        Point(0, 1),
        Point(0, -1),
        Point(1, -1),
        Point(1, 0),
        Point(1, 1)
      )

      // Look adjacent to the target square for another king of the same color
      for (kSq <- kingSquares) {
        val adj = Point(new Point(target).x + kSq.x, new Point(target).y + kSq.y)
        if (adj.inBounds) {
          val cPiece = board.getSquare(adj)
          // Wrap this in if cPiece !=0 to avoid processing for no piece found
          if (cPiece != null) {

            if (cPiece.name == "king" && cPiece.color == color) {
              notation.isDoubleThreat = true

              // Compare file, if the same, then
              if (fromPoint.y == adj.y) {
                notation.rankNeeded = true
              }
              // Compare rank, if the same, then
              if (fromPoint.x == adj.x) {
                notation.fileNeeded = true
              }

              // Double threat no common rank or file
              if ((fromPoint.y != adj.y) && (fromPoint.x != adj.x)) {
                notation.fileNeeded = true // Still a double threat and need to identify the king moving
              }
            }
          }
        }
      }
    }
    notation
  }

  def senString: String = {
    val result: StringBuilder = StringBuilder.newBuilder
    val board = Array.ofDim[String](12, 12)

    for ((pos, piece) <- gameBoard) {
      val position = new Point(pos)
      board(12 - position.y)(position.x - 1) = if (piece == null) " " else piece.senString
    }

    for (row <- board) {
      for (piece <- row) {
        if (piece == " ") {
          if (result.nonEmpty && '0' <= result.last && result.last <= '9') {
            var countSpace = result.last.toInt - '0'.toInt
            result.deleteCharAt(result.length - 1)
            if (result.nonEmpty && '0' <= result.last && result.last <= '9') {
              countSpace += (result.last.toInt - '0'.toInt) * 10
              result.deleteCharAt(result.length - 1)
            }
            result.append((countSpace + 1).toString)
          } else {
            result.append("1")
          }
        } else {
          result.append(piece)
        }
      }
      result.append("/")
    }
    result.deleteCharAt(result.length - 1)

    result.toString()
  }
}
