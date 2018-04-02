package synergychess

import nchess.Server
import nchess.rule.{Position, Search, State}

class PositionAdapter extends Position {
  override def getState(moves: Seq[String]): State.Value = State.ALIVE

  override def isLegal(moves: Seq[String], move: String): Boolean = true

  override def getFen(moves: Seq[String]): String = "TODO"

  override def isStalemateDraw: Boolean = true
}

class SearchAdapter extends Search {
  override def searchBestMove(moves: Seq[String], level: Int, onBestMove: String => Unit) {
    onBestMove("TODO")
  }
}

object Boot {
  def main(args: Array[String]) {
    Server.boot(new PositionAdapter, new SearchAdapter)
  }
}
