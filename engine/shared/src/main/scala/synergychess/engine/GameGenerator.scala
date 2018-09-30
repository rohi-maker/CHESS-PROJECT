package synergychess.engine

object GameGenerator {
  val startingSEN = "r1n1bkqb1n1r/2p6p2/1prnbqkbnrp1/pppppppppppp/12/12/12/12/PPPPPPPPPPPP/1PRNBQKBNRP1/2P6P2/R1N1BKQB1N1R w KQkq KQkq - 0 0"

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
          val pos = Point(col, rank)
          val nPiece = PieceFactory.newPiece(c, pos)
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
}
