package synergychess.engine

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class CastlingSpec extends FlatSpec with Matchers with BeforeAndAfter {
  val castlingConfig = "r1n1bk5r/1npb1qqnbp2/1pr3kbnrp1/pppppppppppp/12/12/11P/12/PPPPPPPPPPP1/1PR3K2RP1/2P1NBB2PN1/R4K5R w KQkq KQkq - 0 6"

  var game: Game = Game()
  var board: Board = new Board()
  var moveData: MoveData = new MoveData()

  before {
    game = GameGenerator.loadFromSEN(castlingConfig)
    board = game.board
    moveData = new MoveData()
  }

  "(inner)" should "be able to castle king side" in {
    val king = board.getSquare("G3")
    val rook = board.getSquare("J3")
    moveData.from = "G3"
    moveData.to = "I3"
    game.move(moveData)

    board.getSquare("G3") shouldBe null
    board.getSquare("J3") shouldBe null
    board.getSquare("I3") shouldBe king
    board.getSquare("H3").senString shouldBe rook.senString
    game.castling.status("G3") shouldBe Array(false, false)
  }

  "(inner)" should "be able to castle queen side" in {
    val king = board.getSquare("G3")
    val rook = board.getSquare("C3")
    moveData.from = "G3"
    moveData.to = "E3"
    game.move(moveData)

    board.getSquare("G3") shouldBe null
    board.getSquare("C3") shouldBe null

    board.getSquare("E3").senString shouldBe king.senString
    board.getSquare("F3").senString shouldBe rook.senString
    game.castling.status("G3") shouldBe Array(false,false)
  }

  "(outer)" should "be able to castle king side" in {
    val king = board.getSquare("F1")
    val rook = board.getSquare("A1")
    moveData.from = "F1"
    moveData.to = "B1"
    moveData.rookPlacement = "D1"

    game.move(moveData)

    board.getSquare("F1") shouldBe null
    board.getSquare("B1").senString shouldBe king.senString
    board.getSquare("D1").senString shouldBe rook.senString
    game.castling.status("F1") shouldBe Array(false,false)
  }

  "(outer)" should "be able to castle queen side" in {
    val king = board.getSquare("G3")
    val rook = board.getSquare("C3")
    moveData.from = "G3"
    moveData.to = "E3"

    game.move(moveData)

    board.getSquare("G3") shouldBe null
    board.getSquare("C3") shouldBe null

    board.getSquare("E3").senString shouldBe king.senString
    board.getSquare("F3").senString shouldBe rook.senString
    game.castling.status("G3") shouldBe Array(false,false)
  }
}
