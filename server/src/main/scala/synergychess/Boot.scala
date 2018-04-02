package synergychess

import nchess.Server
import nchess.rule.{Position, State}

class PositionAdapter extends Position {
  override def getState(moves: Seq[String]): State.Value = State.ALIVE

  override def isLegal(moves: Seq[String], move: String): Boolean = true

  override def getFen(moves: Seq[String]): String = "TODO"

  override def isStalemateDraw: Boolean = true
}

object Boot {
  def main(args: Array[String]) {
    Server.boot(new PositionAdapter)
  }
}
