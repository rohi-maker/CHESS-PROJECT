package synergychess.engine

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

  val pieceNames = Array("pawn", "king", "queen", "king", "rook", "bishop", "knight")

  var board = new Board()
  var castling = new Castling()
  var endConditions = new EndConditions()
  var teamToMove = "white"
  var moveHistory: ArrayBuffer[MoveData] = ArrayBuffer[MoveData]()
  var removedKingChoices: ArrayBuffer[String] = ArrayBuffer[String]()
  // OPS notation
  var moveArray: ArrayBuffer[String] = ArrayBuffer[String]()
  var moveNumber = 1

  var enPassant = ""
  var validMoves: ArrayBuffer[String] = ArrayBuffer[String]()

  def move(moveData: MoveData): Option[MoveResult] = {
    var result = new MoveResult()
    moveData.board = board
    moveData.enPassant = enPassant
    moveData.castling = castling

    if (removedKingChoices.contains(moveData.kingChoice)) {
      board.setSquare(moveData.kingChoice, null)
      moveHistory.append(moveData)
      val mate = endConditions.getMateData(board, teamToMove, enPassant)
      result.completed = false
      result.mateData = mate
      result.mInfo = moveData
    }

    val pMoved = board.getSquare(moveData.from)
    if (pMoved == null) return None
    validMoves = pMoved.validMoves(moveData)

    // VALIDATE PROMOTION MOVE
    var legalMove = validMoves.contains(moveData.to)

    val notation = new Notation()
    if (moveData.promotionData != null){
      val kingSq: String = moveData.promotionData.kingPlacement

      val vDs = validPromotions(teamToMove, moveData.to)
      val safeSqs = vDs._1
      val valids = vDs._2
      moveData.promotionData.validPieces = valids
      moveData.promotionData.safeSqs = safeSqs

      notation.isPromotion = true
      if (moveData.promotionData.name != "" && !valids.contains(moveData.promotionData.name)) return None
      if (kingSq != "") legalMove = safeSqs.contains(kingSq)
    }

    // VALIDATE CASTLING MOVE
    // !!needs to be added

    if (legalMove) {
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
      Some(result)
    } else None
  }

  def validPromotions(color: String, to: String): (ArrayBuffer[String], ArrayBuffer[String]) = {
    val valids = ArrayBuffer[String]()
    val promoSqValue: Piece = Piece.newPiece(board.getSquare(to), to)
    
    if (promoSqValue != null) board.setSquare(to, null)
    
    val captured = piecesCaptured(teamToMove)
    val safeSqs = board.safeSquares(teamToMove)

    for (p <- pieceNames) {
      if (captured(p) != 0) {
        // KING not valid promotion if no safe Squares
        if (safeSqs.isEmpty && p == "king") return null
        if (p != "pawn") valids.append(p)
      }
    }

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
}
