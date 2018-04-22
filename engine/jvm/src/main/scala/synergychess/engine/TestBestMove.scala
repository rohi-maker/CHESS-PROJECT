package synergychess.engine

object TestBestMove {
  def main(args: Array[String]) {
    val game = GameGenerator.loadFromSEN(GameGenerator.startingSEN)

    while (true) {
      println(game.board.senString)

      game.nextBestMove match {
        case None =>
          throw new RuntimeException("Could not get next best move")

        case Some(moveData) =>
          game.move(moveData) match {
            case None =>
              throw new RuntimeException("Could not play best move (invalid?)")

            case Some(moveResult) =>
              if (moveResult.mateData.trueCheckMate) {
                println("True checkmate")
                System.exit(0)
              } else if (moveResult.mateData.staleMate) {
                println("Sheckmate")
                System.exit(0)
              }
          }
      }
    }
  }
}
