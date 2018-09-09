package synergychess.engine

import scala.util.Random

object NegamaxBestMove {
  private val INF = 9999

  def getBestMove(game: Game, depth: Int): Option[MoveData] =
    getBestMove(game, depth, -INF, INF)._2

  private def getBestMove(pGame: Game, depth: Int, alpha: Int, beta: Int): (Int, Option[MoveData]) = {
    var game = pGame
    val moves = game.possibleMoves

    if (depth == 0 || moves.isEmpty) {
      return (evaluate(game, game.teamToMove), None)
    }

    val listOfMoves = Random.shuffle(moves)
    var a = alpha

    var value = -INF
    var bestMove: Option[MoveData] = None
    val currSEN = game.senString

    for (move <- listOfMoves) {
      game = GameGenerator.loadFromSEN(currSEN)
      game.move(move)

      val child = -getBestMove(game, depth - 1, -beta, -a)._1
      if (value < child) {
        value = child
        bestMove = Some(move)
      }
      a = math.max(a, value)
      if (alpha >= beta) return (value, bestMove)
    }

    (value, bestMove)
  }

  private def evaluate(game: Game, color: String): Int = {
    var value: Int = 0

    game.board.gameBoard.foreach {
      case (_, piece) => if (piece != null) {
        value += piece.value * (if (piece.color == "white") 1 else -1)
      }
    }

    value * (if (color == "white") 1 else -1)
  }
}
