package synergychess.engine

object TestBestMove {
  def main(args: Array[String]) {
    loopUntilGameOver()
  }

  private def loopUntilGameOver() {
    val game = GameGenerator.loadFromSEN(GameGenerator.startingSEN)

    while (true) {
      println(game.senString)

      game.nextBestMove match {
        case None =>
          throw new RuntimeException("Could not get next best move")

        case Some(moveData) =>
          println(s"Best move: $moveData")

          game.move(moveData) match {
            case None =>
              throw new RuntimeException("Could not play best move (invalid?)")

            case Some(moveResult) =>
              val mateData = moveResult.mateData

              if (mateData.trueCheckMate) {
                println("True check mate")
                System.exit(0)
              } else if (mateData.staleMate) {
                println("Stale mate")
                System.exit(0)
              } else if (mateData.checkMate) {
                println("Check mate")
              }
          }
      }
    }
  }
}
