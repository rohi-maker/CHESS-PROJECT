package synergychess.engine

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class BestMoveSpec extends FlatSpec with Matchers with BeforeAndAfter {
  "Best move" should "be valid" in {
    val game = GameGenerator.loadFromSEN(GameGenerator.startingSEN)
    val moveDataOpt = game.nextBestMove
    moveDataOpt should not be None

    val moveData = moveDataOpt.get

    val possibleMoves = game.possibleMoves.map(_.toString)
    possibleMoves.contains(moveData.toString) shouldBe true

    game.move(moveData) should not be None
  }
}

