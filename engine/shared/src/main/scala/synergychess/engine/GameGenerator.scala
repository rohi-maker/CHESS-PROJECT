package synergychess.engine

object GameGenerator {
  def loadFromSEN(senString: String): Game = {
    val game = Game()
    val senArray = senString.split(" ")
    val configuration = senArray(0).split("/")
    val teamToMove = if (senArray(1) == "w") "white" else "black"
    val whiteCastleConfig = senArray(2)
    val blackCastleConfig = senArray(3)
    val enPassantSq = if (senArray(4) == "-") "" else senArray(4).toUpperCase()
    val moveCount = senArray(6).toInt
    val board = new Board()
    val castling = new Castling()
    castling.configure(whiteCastleConfig,blackCastleConfig)

    // Populate the game board from configuration
    for (rank <- 12 until 0 by -1){
      val rowConfig = configuration(12 - rank)
      var col = 1
      var count = 0
      while (col <= 12){
        val c = rowConfig(count)
        var tmp = c.toString
        if (!(47 < c && c < 58)) {
          val pos = Point(col, rank).toString()
          val nPiece = PieceFactory.newPiece(getPieceName(c.toString), getPieceTeam(c.toString), new Point(pos))
          board.setSquare(pos, nPiece)
          col += 1
          count += 1
        } else {
          if (count + 1 < rowConfig.length && 47 < rowConfig(count + 1) && rowConfig(count + 1) < 58) {
            tmp = rowConfig.substring(count, count + 2)
            count += 2
          } else {
            count += 1
          }

          for (_ <- 1 to tmp.toInt) {
            val pos = Point(col, rank).toString
            board.setSquare(pos, null)
            col += 1
          }
        }
      }
    }
    game.moveNumber = moveCount
    game.board = board
    game.castling = castling
    game.enPassant = if (enPassantSq == "-") "" else enPassantSq
    game.teamToMove = teamToMove

    game
  }

  def getPieceName(pchar: String): String = {
    val pChar = pchar.toUpperCase()
    pChar match {
      case "P" =>  "pawn"
      case "N" =>  "knight"
      case "B" =>  "bishop"
      case "R" =>  "rook"
      case "Q" =>  "queen"
      case "K" =>  "king"
      case "I" =>  "infantry"
    }
  }

  def getPieceTeam(pChar: String): String = {
    if (pChar.toUpperCase() == pChar) "white" else "black"
  }
}
