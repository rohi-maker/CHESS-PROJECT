package synergychess.engine

import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable.ArrayBuffer

class CheckMateSpec extends FlatSpec with Matchers {
  var game: Game = Game()
  var board: Board = new Board()
  var moveData: MoveData = new MoveData()

  "" should "be checkMate and either king to be removed" in {
    var cSen = "r1n1bkqb1n1r/1np6p2/1pr3kbnrp1/pppppb2pppp/6p5/12/5Q6/12/PPPPP2PPPPP/1PRNB1KBNRP1/2P2Q3P2/R1N1BK1B1N1R w KQkq KQkq - 0 6"

    game = GameGenerator.loadFromSEN(cSen)
    board = game.board
    moveData = new MoveData()
    moveData.board = board
    moveData.castling = new Castling()

    moveData.from = "F6"
    moveData.to = "F9"

    val result = game.move(moveData)

    result.get.mateData.checkMate shouldBe true
    result.get.mateData.trueCheckMate shouldBe false
    result.get.mateData.kingsDead.isEmpty shouldBe true
    result.get.mateData.choices shouldBe ArrayBuffer("F12","G10")
  }

  "" should "be True Checkmate, i.e. Game Over" in {
    val cSen = "B2kbQQ5/2pk6nn/9pp1/pp5p1p1p/3N6p1/B7pn2/9PP1/8P3/PPPPP1PP3P/1PRN2K2RP1/2P6P2/R1N1BK1B1N1R w KQkq Qk - 0 2"
    game = GameGenerator.loadFromSEN(cSen)
    board = game.board
    moveData = new MoveData()
    moveData.board = new Board()
    moveData.castling = new Castling()

    moveData.from = "F12"
    moveData.to = "E12"
    val result = game.move(moveData)

    result.get.mateData.trueCheckMate shouldBe true
  }

  "" should "checkmate with only one valid king to remove" in {
    val cSen = "4rkb3qr/n1pqnb2Qpnn/1p3rk3p1/1p1ppp1pprpp/p8p2/12/3Q8/2B4PP3/PPP1P1P1BPPP/1PRN2K1NRP1/2P6P2/R1N1BK1B1N1R w KQkq Q - 0 12"
    game = GameGenerator.loadFromSEN(cSen)
    board = game.board
    moveData = new MoveData()
    moveData.board = new Board()
    moveData.castling = new Castling()

    moveData.from = "I11"
    moveData.to = "H10"
    val result = game.move(moveData)
    result.get.mateData.trueCheckMate shouldBe false
    result.get.mateData.checkMate shouldBe true
    result.get.mateData.kingsDead shouldBe ArrayBuffer("G10")
  }
}
