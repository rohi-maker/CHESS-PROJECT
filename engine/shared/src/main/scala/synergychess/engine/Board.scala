package synergychess.engine

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

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

  def squaresAttackedBy: Map[String, scala.collection.mutable.Map[String, ArrayBuffer[String]]] = {
    // Return squares attacked by each team
    val white = scala.collection.mutable.Map[String, ArrayBuffer[String]]()
    val black = scala.collection.mutable.Map[String, ArrayBuffer[String]]()

    for (pos <- gameBoard.keys) {
      val v = getSquare(pos)

      if (v != null) {
        val color = v.color
        val sqsAtk = v.squaresAttacking(this, pos.toString)
        val moveInfo = new MoveData()
        moveInfo.from = v.basePos.toString
        moveInfo.board = this
        moveInfo.moveList = sqsAtk.distinct
        val valids = v.validMoves(moveInfo)
        val sqsAtkd = sqsAtk.filter(valids.contains(_))

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

  def inCheck(color: String): Boolean = {
    // Checks if color given is in check
    var inCheck = false
    val kings = this.getKingPositions(color)
    val orthogs = Array(Array(1, 0), Array(-1, 0), Array(0, 1), Array(0, -1))
    val diags = Array(Array(1, 1), Array(-1, 1), Array(1, -1), Array(-1, -1))

    for (pos <- kings) {
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
        breakable {
          val cDir = king.step(this, new Point(pos), offset(0), offset(1))
          if (cDir.isEmpty) break()

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

            if (kingColor == "black" && threatDirection == 1) {  // black king looking "up" at target pawn
              break
            } else if (kingColor == "white" && threatDirection == -1) {  // white king looking "down" at target pawn
              break
            } else {  // pawn is a threat
              return true
            }
          }

          // Check for Queen or Bishop
          if (target.isInstanceOf[Queen] || target.isInstanceOf[Bishop]) {
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
    }

    inCheck
  }

  def checkDoubleThreat(attackingPieceName: String, to: String, from: String, board: Board, color: String): Notation = {
    // Checks if a square is threatened by two of the same type pieces
    // So notation can represent the move correctly
    // Returns true or false

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
    var foundPiece: Piece = null
    val notation = new Notation()

    // Pawns - doublethreat calcs not needed as pawns show file when taking by default

    //  KNIGHT Doublethreats
    if (attackingPieceName == "knight") {  // If attackingpiece is a knight
      // Check the target square for any other knights threatening it
      for (nSq <- knightSquares) {
        val adj = Point(new Point(target).x + nSq.x, new Point(target).y + nSq.y)
        val foundPiece = board.getSquare(adj)

        // Don't process for "adjacent" squares which are out of bounds (x or y <= 0 -> undefined ), or empty squares (foundPiece = 0)
        if (foundPiece != null) {
          if (foundPiece.isInstanceOf[Knight] && foundPiece.color == color) {
            // Val adjKnightPos = adj.toString()
            notation.isDoubleThreat = true

            // Compare file, if the same, then
            if (from.charAt(0) == adj.toString.charAt(0)) {
              notation.rankNeeded = true
            }

            // Compare rank, if the same, then
            if (from.charAt(1) == adj.toString.charAt(1)) {
              notation.fileNeeded = true
            }

            // If a doublethreat with no common rank or file
            if ((from.charAt(0) != adj.toString.charAt(0)) && (from.charAt(1) != adj.toString().charAt(1))) {
              if (!notation.fileNeeded) notation.fileNeeded = true
              // and it's the last square looked at (nSq from knightSquares()) not distinguished already by a prior piece found
              if (nSq.x == -2 && nSq.y == -1 && !notation.rankNeeded && !notation.fileNeeded) {
                notation.fileNeeded = true
              }
            }
          }
        }
      }
      // If so, of the two knights
      // If x is the same then show the file
      // If y is the same then show the rank
    }


    // DIAGONALS - queens and bishops
    if (attackingPieceName == "queen" || attackingPieceName == "Q" || attackingPieceName == "bishop" || attackingPieceName == "B") {
      // Check diagonals
      val diagPieces = ArrayBuffer[String]()

      // Inline diagonals case where the piece is moving away from another behind it
      // Filter out the direction the piece came *from* by removing that directional element in the diags array.
      // Refer val diags = ((1,1),(-1,1),(1,-1),(-1,-1))
      if (new Point(from).x < new Point(to).x && new Point(from).y < new Point(to).y) diags.remove(3,1)  // -1 -1 case
      if (new Point(from).x < new Point(to).x && new Point(from).y > new Point(to).y) diags.remove(1,1)  // -1 +1 case
      if (new Point(from).x > new Point(to).x && new Point(from).y < new Point(to).y) diags.remove(2,1)  // +1 -1 case
      if (new Point(from).x > new Point(to).x && new Point(from).y > new Point(to).y) diags.remove(0,1)  // +1 +1 case


      // For each direction have a look diagonally and record the last square found
      for (offset <- diags) {
        val diagSquares = board.getSquare(target).ownTeamStep(board, new Point(target), offset(0), offset(1))
        // Add last square found may or may not be a piece (ie could be board edge empty square)
        val lastSquare = board.getSquare(diagSquares(diagSquares.length - 1))

        // Filter undefined (square against edge) and empty squares
        if (lastSquare != null) {
          diagPieces.append(diagSquares(diagSquares.length - 1))
        }
      }

      // Check those squares for the same type of piece
      for (pos <- diagPieces) {
        foundPiece = board.getSquare(pos)

        if (foundPiece.name == attackingPieceName && foundPiece.color == color) {
          notation.isDoubleThreat = true
          // If the found piece has the same rank as where the attacking piece is coming from
          // Tell notation to show the file
          if ((from.charAt(1) == pos.charAt(1)) && (from.charAt(2) == pos.charAt(2))) notation.fileNeeded = true
          if (from.charAt(0) == pos.charAt(0)) notation.rankNeeded = true//ditto file
          if (from.charAt(0)!= pos.charAt(0) && from.charAt(1)!= pos.charAt(1)) notation.fileNeeded = true
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

      if (new Point(from).x < new Point(to).x && new Point(from).y == new Point(to).y) orthogs.remove(1,1)  // Moves to right
      if (new Point(from).x > new Point(to).x && new Point(from).y == new Point(to).y) orthogs.remove(0,1)  // Moves to left
      if (new Point(from).x == new Point(to).x && new Point(from).y < new Point(to).y) orthogs.remove(3,1)  // Moves up (rel to x,y scale)
      if (new Point(from).x == new Point(to).x && new Point(from).y > new Point(to).y) orthogs.remove(2,1)  // Moves down (rel to x,y scale)

      for (offset <- orthogs) {
        val orthogSquares = board.getSquare(target).ownTeamStep(board, new Point(target), offset(0), offset(1))
        val lastSquare = board.getSquare(orthogSquares(orthogSquares.length - 1))

        // Add last square found may or may not be a piece (ie could be board edge empty square)
        if (lastSquare != null) {  // Filter empty and undefined squares
          orthogPieces.append(orthogSquares(orthogSquares.length - 1))  // Record the square
        }
      }

      // Check those squares for the same type of piece
      for (pos <- orthogPieces) {
        val foundPiece = board.getSquare(pos.toString)
        if (foundPiece.name == attackingPieceName && foundPiece.color == color) {
          notation.isDoubleThreat = true

          //NB pos is the *other found orthog piece* position

          // Inline moves
          // The moving rook and the found rook are on the same rank, file needed
          if (from.charAt(1) == pos.charAt(1)) notation.fileNeeded = true
          // The moving rook and the found rook are on the same file, rank needed
          if (from.charAt(0) == pos.charAt(0)) notation.rankNeeded = true

          // Intersecting moves
          // The rook is moving along a rank, and another rook is threatening the target square along it's file
          if (from.charAt(0) != target.charAt(0) && target.charAt(0) == pos.charAt(0)) notation.fileNeeded = true
          // The rook is moving along a file, and another rook is threatening the target square along it's rank
          if (from.charAt(1)!= target.charAt(1) && target.charAt(1) == pos.charAt(1)) notation.rankNeeded = true

          return notation
        }
      }
    }

    // KING doublethreats
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
        val cPiece = board.getSquare(adj)
        // Wrap this in if cPiece !=0 to avoid processing for no piece found
        if (cPiece != null) {
          val kingFrom = from

          if (cPiece.name == "king" && cPiece.color == color) {
            val adjKingPos = adj.toString()
            notation.isDoubleThreat = true

            // Compare file, if the same, then
            if (kingFrom.charAt(0) == adjKingPos.charAt(0)) {
              notation.rankNeeded = true
            }
            // Compare rank, if the same, then
            if (kingFrom.charAt(1) == adjKingPos.charAt(1)) {
              notation.fileNeeded = true
            }

            // Doublethreat no common rank or file
            if ((kingFrom.charAt(0) != adjKingPos.charAt(0)) && (kingFrom.charAt(1) != adjKingPos.charAt(1))) {
              notation.fileNeeded = true  // Still a doublethreat and need to identify the king moving
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
          if ('0' <= result.last && result.last <= '9') {
            var countSpace = result.last.toInt - '0'.toInt
            result.deleteCharAt(result.length - 1)
            if ('0' <= result.last && result.last <= '9') {
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
