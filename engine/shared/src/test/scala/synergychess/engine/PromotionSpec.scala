package synergychess.engine

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class PromotionSpec extends FlatSpec with Matchers with BeforeAndAfter {
  val senConfig = "r1n2k5r/2p2b3P2/1prn2k5/ppp1p5pp/3p4n1p1/12/3b6P1/11P/PPPPPbBPP3/1PRNB5P1/2PB3q1P1N/2NKR2B3R w KQkq KQk - 0 0"
  var game: Game = Game()
  var board: Board = new Board()
  var moveData: MoveData = new MoveData()

  before {
    game = GameGenerator.loadFromSEN(senConfig)
    board = game.board
    moveData = new MoveData()
    moveData.board = board
    moveData.castling = new Castling()
  }

  "" should "be able to promote to Queen" in {
    moveData.from = "J11"
    moveData.to = "J12"
    moveData.promotionData = new PromotionData()
    moveData.promotionData.name = "queen"
    game.move(moveData)
    board.getSquare("J11") shouldBe null
    board.getSquare("J12").name shouldBe "queen"
  }

  "" should "not be able to promote to Bishop (4 bishops already)" in {
    moveData.from = "J11"
    moveData.to = "J12"
    val p = board.getSquare("J11")
    moveData.promotionData = new PromotionData()
    moveData.promotionData.name = "bishop"
    game.move(moveData)
    board.getSquare("J11").senString shouldBe p.senString
    board.getSquare("J12") shouldBe null
  }

  "" should "be able to promote to Rook by capturing Diagonally to promote" in {
    val senConfig = "r1n2k4r1/2p2b3P2/1prn2k5/ppp1p5pp/3p4n1p1/12/3b6P1/11P/PPPPPbBPP3/1PRNB5P1/2PB3q1P1N/2NKR2B3R w KQkq KQk - 0 0"
    game = GameGenerator.loadFromSEN(senConfig)
    board = game.board
    moveData = new MoveData()
    moveData.board = board
    moveData.castling = new Castling()

    moveData.from = "J11"
    moveData.to = "K12"
    moveData.promotionData = new PromotionData()
    moveData.promotionData.name = "rook"
    game.move(moveData)

    board.getSquare("J11") shouldBe null
    board.getSquare("K12").name shouldBe "rook"
    board.getSquare("K12").color shouldBe "white"
  }

  "" should "not be able to promote to dangerous square" in {
    board = game.board
    val pawn = board.getSquare("J11")
    moveData.from = "J11"
    moveData.to = "A11"
    moveData.promotionData = new PromotionData()
    moveData.promotionData.name = "rook"
    game.move(moveData)

    board.getSquare("J11").senString shouldBe pawn.senString
    board.getSquare("K12") shouldBe null
    board.getSquare("A11") shouldBe null
  }

  "" should "be able to promote by capturing diagonally and place at square previously threatened by captured piece" in {
    board = game.board
    val pawn = board.getSquare("J11")
    moveData.from = "J11"
    moveData.to = "A11"
    moveData.promotionData = new PromotionData()
    moveData.promotionData.name = "rook"
    game.move(moveData)

    board.getSquare("J11").senString shouldBe pawn.senString
    board.getSquare("K12") shouldBe null
    board.getSquare("A11") shouldBe null
  }
}
