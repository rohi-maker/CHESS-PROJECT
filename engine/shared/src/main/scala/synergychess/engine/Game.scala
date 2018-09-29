package synergychess.engine

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

case class Game() {
  val maxPieces = Map(
    "pawn" -> 16,
    "rook" -> 4,
    "knight" -> 4,
    "bishop" -> 4,
    "king" -> 2,
    "queen" -> 2
  )
  private val positionOccurrence = new mutable.HashMap[String, Int]()

  val pieceNames = Array("pawn", "king", "queen", "king", "rook", "bishop", "knight")

  var board = new Board()
  var castling = new Castling()
  var enPassant = ""
  var endConditions = new EndConditions()
  var teamToMove = "white"
  var moveHistory: ArrayBuffer[MoveData] = ArrayBuffer[MoveData]()
  // OPS notation
  var moveArray: ArrayBuffer[String] = ArrayBuffer[String]()
  var moveNumber = 1

  var validMoves: ArrayBuffer[String] = ArrayBuffer[String]()

  def move(moveData: MoveData): Option[MoveResult] = {
    var result: MoveResult = MoveResult()
    moveData.board = board
    moveData.enPassant = enPassant
    moveData.castling = castling

    val mate = endConditions.getMateData(board, teamToMove, enPassant)

    // If check mate and king choice occurs in the valid king to remove list
    if (mate.choices.contains(moveData.kingChoice)) {
      // Remove the selected king
      board.setSquare(moveData.kingChoice, null)

      moveHistory.append(moveData)

      val mate = endConditions.getMateData(board, teamToMove, enPassant)
      result.completed = false
      result.mateData = mate
      result.mInfo = moveData
    }

    val pMoved = board.getSquare(moveData.from)

    // If no piece at from square
    if (pMoved == null) return None

    validMoves = pMoved.validMoves(moveData)

    // VALIDATE PROMOTION MOVE
    var isLegalMove = validMoves.contains(moveData.to)

    val notation = Notation()

    if (moveData.promotionData != null) {
      val kingSq: String = moveData.promotionData.kingPlacement

      val vDs = validPromotions(teamToMove, moveData.to)
      val safeSqs = vDs._1
      val valids = vDs._2
      moveData.promotionData.validPieces = valids
      moveData.promotionData.safeSqs = safeSqs

      notation.isPromotion = true
      if (moveData.promotionData.name != "" && !valids.contains(moveData.promotionData.name)) return None
      if (kingSq != "") isLegalMove = safeSqs.contains(kingSq)
    }

    // VALIDATE CASTLING MOVE

    if (isLegalMove) {
      moveHistory.append(moveData)
      result = pMoved.move(moveData)
      enPassant = result.enPassant
      if (result == null) return None
      // Update board using move result
      for (sq <- result.sq) {
        val pos = sq._1
        val value = sq._2

        if(value != null && value.name == "pawn"){
          value.asInstanceOf[Pawn].doubleJump = false
        }
        val newPos = new Point(pos)
        if (value != null) {
          value.basePos.x = newPos.x
          value.basePos.y = newPos.y
        }
        board.setSquare(pos, value)
      }

      if (teamToMove == "black") moveNumber += 1
      teamToMove = negateTurn(teamToMove)
      castling.trigger(moveData.from)
      castling.trigger(moveData.to)
      validMoves = null
      val mate = endConditions.getMateData(board, teamToMove, enPassant)
      result.completed = true
      result.mateData = mate
      result.mInfo = moveData

      // Check Threefold repetition
      val boardSEN = board.senString // boardSEN only contains the current position of board
      if (!positionOccurrence.contains(boardSEN)) {
        positionOccurrence.update(boardSEN, 0)
      }
      positionOccurrence(boardSEN) += 1
      if (positionOccurrence(boardSEN) == 3) {
        result.mateData.staleMate = true
      }

      Some(result)
    } else None
  }

  def move(moveData: MoveData, isGuarantee: Boolean): Option[MoveResult] = {
    if (!isGuarantee) return move(moveData)

    var result: MoveResult = MoveResult()
    moveData.board = board
    moveData.enPassant = enPassant
    moveData.castling = castling

    val pMoved = board.getSquare(moveData.from)

    moveHistory.append(moveData)
    result = pMoved.move(moveData)
    enPassant = result.enPassant
    // Update board using move result
    for (sq <- result.sq) {
      val pos = sq._1
      val value = sq._2

      if(value != null && value.name == "pawn"){
        value.asInstanceOf[Pawn].doubleJump = false
      }
      val newPos = new Point(pos)
      if (value != null) {
        value.basePos.x = newPos.x
        value.basePos.y = newPos.y
      }
      board.setSquare(pos, value)
    }

    if (teamToMove == "black") moveNumber += 1
    teamToMove = negateTurn(teamToMove)
    castling.trigger(moveData.from)
    castling.trigger(moveData.to)
    validMoves = null
    val mate = endConditions.getMateData(board, teamToMove, enPassant)
    result.completed = true
    result.mateData = mate
    result.mInfo = moveData

    // Check Threefold repetition
    val boardSEN = board.senString // boardSEN only contains the current position of board
    if (!positionOccurrence.contains(boardSEN)) {
      positionOccurrence.update(boardSEN, 0)
    }
    positionOccurrence(boardSEN) += 1
    if (positionOccurrence(boardSEN) == 3) {
      result.mateData.staleMate = true
    }

    Some(result)
  }

  def validPromotions(color: String, to: String): (ArrayBuffer[String], ArrayBuffer[String]) = {
    val valids = ArrayBuffer[String]()

    // Temporary remove promoted piece
    val promoSqValue: Piece = Piece.newPiece(board.getSquare(to), to)
    if (promoSqValue != null) board.setSquare(to, null)
    
    val captured = piecesCaptured(teamToMove)
    val safeSqs = board.safeSquares(teamToMove)

    for (p <- pieceNames) {
      if (captured(p) != 0) {
        // KING not valid promotion if no safe Squares
        if (safeSqs.isEmpty && p == "king") return null
        if (p != "pawn" && p != "king") valids.append(p)
      }
    }

    // Set board back to the state before removing
    if (promoSqValue != null) board.setSquare(to, promoSqValue)

    (safeSqs, valids)
  }

  def piecesCaptured(color: String): scala.collection.mutable.Map[String, Int] = {
    val captured = scala.collection.mutable.Map[String, Int]()
    val count = countPieces(color)
    for (name <- maxPieces.keys) {
      captured(name) = maxPieces(name) - count(name)
      // OPS build image reference array here to show lost pieces
    }
    captured
  }

  def countPieces(color: String): Map[String, Int] = {
    val count = scala.collection.mutable.Map(
      "pawn" -> 0,
      "rook" -> 0,
      "knight" -> 0,
      "bishop" -> 0,
      "king" -> 0,
      "queen" -> 0
    )

    for (piece <- board.gameBoard.values) {
      if (piece != null && (piece.color == color)) {
        count(piece.name) += 1
      }
    }

    count.toMap
  }

  def negateTurn(t: String): String = {
    if (t == "white") "black" else "white"
  }

  def senString: String = {
    val result: StringBuilder = StringBuilder.newBuilder

    result.append(board.senString)
    result.append(' ')
    result.append(teamToMove.charAt(0))
    result.append(' ')
    result.append(castling.senString())
    result.append(' ')
    result.append(if (enPassant == "") "-" else enPassant)
    result.append(" 0 ")
    result.append(moveNumber.toString)


    result.toString()
  }

  def possibleMoves: Seq[MoveData] = {
    val ret = new ArrayBuffer[MoveData]()

    def realPossibleMoves(kingChoice: String): Seq[MoveData] = {
      val ret = new ArrayBuffer[MoveData]()

      // Reuse
      val from = MoveData()
      from.board = board
      var savedKing: Option[King] = None
      if (kingChoice != "") {
        savedKing = Some(from.board.gameBoard(kingChoice).asInstanceOf[King])
        from.board.gameBoard(kingChoice) = null
      }
      from.castling = castling
      from.enPassant = enPassant

      for (piece <- from.board.gameBoard) {
        if (piece._2 != null && piece._2.color == teamToMove) {
          from.from = piece._1
          val validMoves = piece._2.validMoves(from)

          for (validMove <- validMoves) {
            // Cannot reuse "from" above
            val moveData = MoveData()
            moveData.from = from.from
            moveData.to = validMove
            moveData.board = board
            moveData.castling = castling
            moveData.enPassant = enPassant
            moveData.kingChoice = kingChoice

            ret.append(moveData)
          }
        }
      }
      if (kingChoice != "") from.board.gameBoard(kingChoice) = savedKing.get

      ret
    }

    val mate = endConditions.getMateData(board, teamToMove, enPassant)
    if (mate.checkMate) {
      if (mate.kingsDead.nonEmpty) {
        for (kingChoice <- mate.kingsDead) {
          ret.appendAll(realPossibleMoves(kingChoice))
        }
      } else {
        ret.appendAll(realPossibleMoves(""))
        if (ret.isEmpty) {
          for (kingChoice <- mate.safeKings) {
            ret.appendAll(realPossibleMoves(kingChoice))
          }
        }
      }
    } else {
      ret.appendAll(realPossibleMoves(""))
    }

    ret
  }

  def nextBestMove(level: Int): Option[MoveData] = {
    NegamaxBestMove.getBestMove(this, level)
  }
}
