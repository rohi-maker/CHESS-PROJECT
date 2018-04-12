package synergychess.engine

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class PieceMovementSpec extends FlatSpec with Matchers with BeforeAndAfter {
  var startingSEN = "r1n1bkqb1n1r/2p6p2/1prnbqkbnrp1/pppppp1p2pp/12/6p1pP2/5P6/12/PPPPP1PP1PPP/1PRNBQKBNRP1/2P6P2/R1N1BKQB1N1R w KQkq KQkq I8 0 3"
  var game: Game = Game()
  var board: Board = new Board()
  var moveInfo: MoveData = new MoveData()

  before {
    game = GameGenerator.loadFromSEN(startingSEN)
    board = game.board
    moveInfo = new MoveData()
    moveInfo.board = board
    moveInfo.castling = new Castling()
  }

  "Pawn" should "be able to move two squares on first move" in  {
    moveInfo.from = "G4"
    moveInfo.to = "G6"
    game.move(moveInfo)
    board.getSquare("G6").basePos.toString shouldBe "G6"
  }

  "Pawn" should "be able to capture diagonally" in {
    val p1 = board.getSquare("F6")
    moveInfo.from = "F6"
    moveInfo.to = "G7"
    game.move(moveInfo)
    board.getSquare("G7").senString shouldBe p1.senString
    board.getSquare("F6") shouldBe null

  }

  "Pawn" should "be able to capture enPassant" in {
    val p1 = board.getSquare("J7")
    moveInfo.from = "J7"
    moveInfo.to = "I8"
    game.move(moveInfo)
    board.getSquare("I8").senString shouldBe p1.senString
    board.getSquare("J7") shouldBe null
    board.getSquare("I7") shouldBe null
  }

  "Knight" should "be able to move L shape to empty Square" in  {
    val n1 = board.getSquare("D3")
    moveInfo.from = "D3"
    moveInfo.to = "E5"
    game.move(moveInfo)
    board.getSquare("E5").senString shouldBe n1.senString
    board.getSquare("D3") shouldBe null
  }


  "Knight" should "not be able to move L shape to square occupied by ally piece" in  {
    val n1 = board.getSquare("D3")
    moveInfo.from = "D3"
    moveInfo.to = "B4"
    game.move(moveInfo)
    board.getSquare("B4") should not be n1
    board.getSquare("D3").senString shouldBe n1.senString
  }

  "Rook" should "be able to move Horizontally" in  {
    val r1 = board.getSquare("A1")
    moveInfo.from = "A1"
    moveInfo.to = "B1"
    game.move(moveInfo)
    board.getSquare("B1").senString shouldBe r1.senString
    board.getSquare("A1") shouldBe null
  }

  "Rook" should "be able to move Vertically" in  {
    val r1 = board.getSquare("A1")
    moveInfo.from = "A1"
    moveInfo.to = "A3"
    game.move(moveInfo)
    board.getSquare("A3").senString shouldBe r1.senString
    board.getSquare("A1") shouldBe null
  }

  "Rook" should "not be able to through a piece" in  {
    val r1 = board.getSquare("A1")
    moveInfo.from = "A1"
    moveInfo.to = "A6"
    game.move(moveInfo)
    board.getSquare("A6") should not be r1
    board.getSquare("A1").senString shouldBe r1.senString
  }

  "Bishop" should "be able to move Diagonally" in  {
    val cPiece = board.getSquare("H1")
    moveInfo.from = "H1"
    moveInfo.to = "I2"
    game.move(moveInfo)
    board.getSquare("I2").senString shouldBe cPiece.senString
    board.getSquare("H1") shouldBe null
  }

  "Bishop" should "not be able to move orthoganally" in  {
    val cPiece = board.getSquare("H1")
    moveInfo.from = "H1"
    moveInfo.to = "H2"
    game.move(moveInfo)
    board.getSquare("H1").senString shouldBe cPiece.senString
    board.getSquare("H2") should not be cPiece
  }

  "Bishop" should "not be able to through a piece" in  {
    val cPiece = board.getSquare("H1")
    moveInfo.from = "H1"
    moveInfo.to = "J3"
    game.move(moveInfo)
    board.getSquare("J3") should not be cPiece
    board.getSquare("H1").senString shouldBe cPiece.senString
  }

  "Queen" should "be able to move Diagonally" in  {
    val cPiece = board.getSquare("F3")
    moveInfo.from = "F3"
    moveInfo.to = "G2"
    game.move(moveInfo)
    board.getSquare("G2").senString shouldBe cPiece.senString
    board.getSquare("F3") shouldBe null
  }

  "Queen" should "be able to move orthoganally" in  {
    val cPiece = board.getSquare("F3")
    moveInfo.from = "F3"
    moveInfo.to = "F2"
    game.move(moveInfo)
    board.getSquare("F2").senString shouldBe cPiece.senString
    board.getSquare("F3") should not be cPiece
  }

  "Queen" should "not be able to through a piece" in  {
    val cPiece = board.getSquare("F3")
    moveInfo.from = "F3"
    moveInfo.to = "F8"
    game.move(moveInfo)
    board.getSquare("F8") should not be cPiece
    board.getSquare("F3").senString shouldBe cPiece.senString
  }

  "King" should "be able to move Diagonally one square" in  {
    val cPiece = board.getSquare("F1")
    moveInfo.from = "F1"
    moveInfo.to = "G2"
    game.move(moveInfo)
    board.getSquare("G2").senString shouldBe cPiece.senString
    board.getSquare("F1") shouldBe null
  }

  "King" should "be able to move north one Square" in  {
    val cPiece = board.getSquare("F1")
    moveInfo.from = "F1"
    moveInfo.to = "F2"
    game.move(moveInfo)
    board.getSquare("F2").senString shouldBe cPiece.senString
    board.getSquare("F1") should not be cPiece
  }

  "King" should "not be able to occupied square" in  {
    val cPiece = board.getSquare("F1")
    moveInfo.from = "F1"
    moveInfo.to = "G1"
    game.move(moveInfo)
    board.getSquare("G1") should not be cPiece
    board.getSquare("F1").senString shouldBe cPiece.senString
  }
}
