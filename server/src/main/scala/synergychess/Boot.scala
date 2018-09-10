package synergychess

import nchess.Server
import nchess.api.{MoveInfo, Position, Search, State, StateInfo}

import synergychess.engine.{Game, GameGenerator, MateData, MoveData, MoveResult}

object AdaperUtil {
  def movesToGame(moves: Seq[String]): Option[(Game, MoveResult)] = {
    val game = GameGenerator.loadFromSEN(GameGenerator.startingSEN)

    var moveResult: MoveResult = null

    for (move <- moves) {
      val moveData = new MoveData(move)
      game.move(moveData) match {
        case None =>
          return None

        case Some(mr) =>
          moveResult = mr
      }
    }

    Some((game, moveResult))
  }

  def mateDataToState(numMoves: Int, mateData: MateData): State.Value = {
    if (mateData.trueCheckMate) return if (numMoves % 2 == 0) State.P2_CHECKMATE else State.P1_CHECKMATE

    if (mateData.staleMate) return if (numMoves % 2 == 0) State.P2_STALEMATE else State.P1_STALEMATE

    State.ALIVE
  }
}

class PositionAdapter extends Position {
  override def stateInfo(gameName: String, moves: Seq[String], onStateInfo: Option[StateInfo] => Unit) {
    AdaperUtil.movesToGame(moves) match {
      case None =>
        onStateInfo(None)

      case Some((game, moveResult)) =>
        val state = AdaperUtil.mateDataToState(moves.size, moveResult.mateData)
        val stateInfo = StateInfo(game.senString, state)
        onStateInfo(Some(stateInfo))
    }
  }

  override def stalemateIsDraw(gameName: String): Boolean = true
}

class SearchAdapter extends Search {
  override def searchBestMove(gameName: String, moves: Seq[String], level: Int, onBestMove: Option[MoveInfo] => Unit) {
    AdaperUtil.movesToGame(moves) match {
      case None =>
        onBestMove(None)

      case Some((game, _)) =>
        // First moves should be fast.
        // Limit level to 1; 2+ is slow for now.
        val limit = 1  //if (moves.length < 2) 1 else 3
        game.nextBestMove(Math.min(limit, level)) match {
          case None =>
            onBestMove(None)

          case Some(moveData) =>
            game.move(moveData) match {
              case None =>
                onBestMove(None)

              case Some(moveResult) =>
                val state = AdaperUtil.mateDataToState(moves.size + 1, moveResult.mateData)
                val stateInfo = StateInfo(game.senString, state)
                val moveInfo = MoveInfo(moveData.toString, stateInfo)
                onBestMove(Some(moveInfo))
            }

        }
    }
  }
}

object Boot {
  def main(args: Array[String]) {
    Server.boot(Seq(""), new PositionAdapter, new SearchAdapter)
  }
}
