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

  "nextBestMove" should "return best move after check mate (not true check mate)" in {
    val game = GameGenerator.loadFromSEN("3k8/1P4k1r3/12/2p6p2/2P6P2/12/7B4/11N/n8b2/12/12/3K7K b - - - 0 329")
    val move = "I11I1___"

    // Ensure the move is valid
    val possibleMoves = game.possibleMoves.map(_.toString)
    possibleMoves.contains(move.toString) shouldBe true

    // Make move
    val moveDataOpt = game.move(new MoveData(move))
    moveDataOpt should not be None

    val moveData = moveDataOpt.get
    val mateData = moveData.mateData

    // Ensure game is not over
    mateData.trueCheckMate shouldBe false
    mateData.staleMate shouldBe false

    // Ensure there's check mate
    mateData.checkMate shouldBe true

    // Ensure that we can still get next move because the game is not over
    game.nextBestMove should not be None
  }
}

