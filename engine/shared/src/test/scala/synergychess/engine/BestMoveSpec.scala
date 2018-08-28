package synergychess.engine

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.collection.mutable.ArrayBuffer

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

  "possible moves" should "not contain remove king if not necessary" in {
    val game = GameGenerator.loadFromSEN(GameGenerator.startingSEN)
    val moves = ArrayBuffer[String](
      "G4G6___", "C12A11___", "H3B9___", "D10B9___", "H1I2___", "F12G11___", "I2B9___", "J12L11___", "B9C10___",
      "A12D12___", "C10E12___", "G12F11___", "E12F11___", "E10F11___", "F3G4___", "G11H11___", "G4A10___", "I10H8___",
      "A10A11___", "C9C8___", "A11C11___", "L12J12___", "G6G7___", "G10G11___", "G7H8___", "D12E12___", "H4H6___",
      "K9K7___", "H8G9___", "F9F7___", "G9F10___", "G11F12___", "I3H5___", "J10I10___", "G1I3___", "K7K6___",
      "I3B10___", "H12L8___", "I4I5___", "L8J10___", "D4D5___", "H10G9___", "E3B6___", "A9A8___", "B6E9___",
      "E12D12___", "C11D12___"
    )
    var moveResult: MoveResult = MoveResult()

    for (move <- moves) {
      val moveData = new MoveData(move)
      val res = game.move(moveData)
      res should not be None
      moveResult = res.get
    }

    val possibleMoves = game.possibleMoves
    possibleMoves.length shouldBe 1
    possibleMoves.head.toString shouldBe "F11E12___"
  }
}

