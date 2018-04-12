package synergychess

import nchess.Server
import nchess.api.{MoveInfo, Position, Search, State, StateInfo}

class PositionAdapter extends Position {
  override def stateInfo(gameName: String, moves: Seq[String], onStateInfo: Option[StateInfo] => Unit) {

  }

  override def stalemateIsDraw(gameName: String): Boolean = true
}

class SearchAdapter extends Search {
  override def searchBestMove(gameName: String, moves: Seq[String], level: Int, onBestMove: Option[MoveInfo] => Unit) {

  }
}

object Boot {
  def main(args: Array[String]) {
    Server.boot(Seq(""), new PositionAdapter, new SearchAdapter)
  }
}
