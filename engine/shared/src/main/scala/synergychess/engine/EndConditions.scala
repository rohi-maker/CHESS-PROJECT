package synergychess.engine

import scala.collection.mutable.ArrayBuffer

// Check for conditions that cause the end of a game
// i.e. True Checkmate, Stalemate, Resignation etc

class EndConditions() {
  def staleMate(board: Board, color: String, enPassant: String): Boolean = {
    val moveData = new MoveData()
    moveData.board = board
    moveData.enPassant = enPassant

    // Where team given is team being checked
    var flag = false
    for (pos <- board.gameBoard.keys) {
      val v = board.getSquare(pos)
      if (v != null && v.color == color) {
        moveData.from = pos
        flag = flag || v.validMoves(moveData).nonEmpty
      }
    }
    !flag
  }

  def checkMate(board: Board, color: String): MateData = {
    val mateData = new MateData()
    val attackedSquares = board.attackedPositions(color)
    var doubleCheck = false
    var kings = board.getKingPositions(color)

    // Filter kings not underAttack
    kings = kings.filter(k => attackedSquares.contains(k))
    if (kings.length == 0) return mateData
    if (kings.length == 2) {
      doubleCheck = true
    }

    // Have to check for singleCheckMate
    def singleKingMated(board: Board, king: String): Boolean = {
      val moveData = new MoveData()
      moveData.board = board
      moveData.from = king
      val kValids = board.getSquare(king).validMoves(moveData)
      if (kValids.nonEmpty) return false

      val sqsAtkdBy = board.squaresAttackedBy
      val oppTeam = if (color == "white") "black" else "white"
      val currTeamAtking = sqsAtkdBy(color)
      val oppTeamAtking = sqsAtkdBy(oppTeam)

      val attackers = oppTeamAtking(king)
      if (attackers.isEmpty) return false

      // Can't block/capture a double check
      if (attackers.length >= 2) return true
      val attacker = attackers(0)
      val atkingAtker = if (currTeamAtking.contains(attacker)) currTeamAtking(attacker) else null
      if (atkingAtker != null) {
        for (p <- atkingAtker) {
          // Check if the attackers are pinned
          moveData.from = p
          if (board.getSquare(p).validMoves(moveData).nonEmpty) return false
        }
      }

      // Knight can't be blocked
      if (board.getSquare(attacker).name == "knight") return true

      // Check if check can be blocked without violating pin
      val pointsBtwn = new Point(king).pointsBetween(attacker)
      pointsBtwn.remove(pointsBtwn.length - 1)

      !pointsBtwn.exists(pos => currTeamAtking(pos).exists(p => {
          moveData.from = p
          return board.getSquare(p).validMoves(moveData).contains(pos)
      }))
    }


    if (doubleCheck) {
      def doubleCheckMate(): Boolean = {
        // If double check only valid move is to capture piece, can't block or move
        val moveData = new MoveData()
        moveData.board = board
        val sqsAtkdBy = board.squaresAttackedBy
        val oppTeam = if (color == "white") "black" else "white"
        val currTeamAtking = sqsAtkdBy(color)
        val oppTeamAtking = sqsAtkdBy(oppTeam)

        // Check if multiple pieces are attacking kings
        val att = if (oppTeamAtking.contains(kings(0))) oppTeamAtking(kings(0)) else ArrayBuffer[String]()
        if (oppTeamAtking.contains(kings(1))) att.appendAll(oppTeamAtking(kings(1)))
        val attackers = att.distinct

        if (attackers.isEmpty) return false
        if (attackers.length >= 2) return true

        // Check if attacker can be captured
        val attacker = attackers(0)
        var atkingAtker = if (currTeamAtking.contains(attacker)) currTeamAtking(attacker) else null
        if (atkingAtker != null) {
          atkingAtker = atkingAtker.filter(k => {
            // Check if the attackers are pinned
            moveData.from = k
            return board.getSquare(k).validMoves(moveData).nonEmpty
          })
          if (atkingAtker.nonEmpty) return false
        }
        true
      }

      val dCheckMate = doubleCheckMate()
      if (!dCheckMate) return mateData
      else mateData.checkMate = true

      val safeKings = ArrayBuffer[String]()

      def tryKing(pos: String) {
        val piece = if (kings(0).toString == pos.toString) kings(1) else kings(0)

        // Remove a king and see if still in checkMate
        val oldKing = new King(board.getSquare(piece).asInstanceOf[King])

        board.setSquare(piece, null)

        val mate = singleKingMated(board, pos)

        if (!mate) safeKings.append(pos)
        else mateData.kingsDead.append(pos)

        board.setSquare(piece, oldKing)
      }

      tryKing(kings(0))
      tryKing(kings(1))
      mateData.safeKings = safeKings
    } else {
      if (singleKingMated(board, kings(0))) {
        mateData.kingsDead.append(kings(0))
        mateData.checkMate = true
      }
    }

    if (mateData.kingsDead.length == board.getKingPositions(color).length) mateData.trueCheckMate = true
    else if (mateData.checkMate) {
      mateData.choices = if (mateData.kingsDead.isEmpty)  mateData.safeKings else mateData.kingsDead
    }
    mateData
  }

  def getMateData(board: Board, color: String, enPassant: String): MateData = {
    val mData = checkMate(board, color)
    mData.staleMate = if (mData.checkMate) false else this.staleMate(board,color,enPassant)
    mData
  }
}
