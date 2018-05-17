package synergychess.engine

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class PieceMovementSpec extends FlatSpec with Matchers with BeforeAndAfter {
  var startingSEN = "r1n1bkqb1n1r/2p6p2/1prnbqkbnrp1/pppppp1p2pp/12/6p1pP2/5P6/12/PPPPP1PP1PPP/1PRNBQKBNRP1/2P6P2/R1N1BKQB1N1R w KQkq KQkq I8 0 3"
  var game: Game = Game()
  var board: Board = new Board()
  var moveData: MoveData = new MoveData()

  before {
    game = GameGenerator.loadFromSEN(startingSEN)
    board = game.board
    moveData = new MoveData()
    moveData.board = board
    moveData.castling = new Castling()
  }

  "Pawn" should "be able to move two squares on first move" in  {
    moveData.from = "G4"
    moveData.to = "G6"
    game.move(moveData)
    board.getSquare("G6").basePos.toString shouldBe "G6"
  }

  "Pawn" should "be able to capture diagonally" in {
    val p1 = board.getSquare("F6")
    moveData.from = "F6"
    moveData.to = "G7"
    game.move(moveData)
    board.getSquare("G7").senString shouldBe p1.senString
    board.getSquare("F6") shouldBe null

  }

  "Pawn" should "be able to capture enPassant" in {
    val p1 = board.getSquare("J7")
    moveData.from = "J7"
    moveData.to = "I8"
    game.move(moveData)
    board.getSquare("I8").senString shouldBe p1.senString
    board.getSquare("J7") shouldBe null
    board.getSquare("I7") shouldBe null
  }

  "Knight" should "be able to move L shape to empty Square" in  {
    val n1 = board.getSquare("D3")
    moveData.from = "D3"
    moveData.to = "E5"
    game.move(moveData)
    board.getSquare("E5").senString shouldBe n1.senString
    board.getSquare("D3") shouldBe null
  }


  "Knight" should "not be able to move L shape to square occupied by ally piece" in  {
    val n1 = board.getSquare("D3")
    moveData.from = "D3"
    moveData.to = "B4"
    game.move(moveData)
    board.getSquare("B4") should not be n1
    board.getSquare("D3").senString shouldBe n1.senString
  }

  "Rook" should "be able to move Horizontally" in  {
    val r1 = board.getSquare("A1")
    moveData.from = "A1"
    moveData.to = "B1"
    game.move(moveData)
    board.getSquare("B1").senString shouldBe r1.senString
    board.getSquare("A1") shouldBe null
  }

  "Rook" should "be able to move Vertically" in  {
    val r1 = board.getSquare("A1")
    moveData.from = "A1"
    moveData.to = "A3"
    game.move(moveData)
    board.getSquare("A3").senString shouldBe r1.senString
    board.getSquare("A1") shouldBe null
  }

  "Rook" should "not be able to through a piece" in  {
    val r1 = board.getSquare("A1")
    moveData.from = "A1"
    moveData.to = "A6"
    game.move(moveData)
    board.getSquare("A6") should not be r1
    board.getSquare("A1").senString shouldBe r1.senString
  }

  "Bishop" should "be able to move Diagonally" in  {
    val cPiece = board.getSquare("H1")
    moveData.from = "H1"
    moveData.to = "I2"
    game.move(moveData)
    board.getSquare("I2").senString shouldBe cPiece.senString
    board.getSquare("H1") shouldBe null
  }

  "Bishop" should "not be able to move orthoganally" in  {
    val cPiece = board.getSquare("H1")
    moveData.from = "H1"
    moveData.to = "H2"
    game.move(moveData)
    board.getSquare("H1").senString shouldBe cPiece.senString
    board.getSquare("H2") should not be cPiece
  }

  "Bishop" should "not be able to through a piece" in  {
    val cPiece = board.getSquare("H1")
    moveData.from = "H1"
    moveData.to = "J3"
    game.move(moveData)
    board.getSquare("J3") should not be cPiece
    board.getSquare("H1").senString shouldBe cPiece.senString
  }

  "Queen" should "be able to move Diagonally" in  {
    val cPiece = board.getSquare("F3")
    moveData.from = "F3"
    moveData.to = "G2"
    game.move(moveData)
    board.getSquare("G2").senString shouldBe cPiece.senString
    board.getSquare("F3") shouldBe null
  }

  "Queen" should "be able to move orthoganally" in  {
    val cPiece = board.getSquare("F3")
    moveData.from = "F3"
    moveData.to = "F2"
    game.move(moveData)
    board.getSquare("F2").senString shouldBe cPiece.senString
    board.getSquare("F3") should not be cPiece
  }

  "Queen" should "not be able to through a piece" in  {
    val cPiece = board.getSquare("F3")
    moveData.from = "F3"
    moveData.to = "F8"
    game.move(moveData)
    board.getSquare("F8") should not be cPiece
    board.getSquare("F3").senString shouldBe cPiece.senString
  }

  "King" should "be able to move Diagonally one square" in  {
    val cPiece = board.getSquare("F1")
    moveData.from = "F1"
    moveData.to = "G2"
    game.move(moveData)
    board.getSquare("G2").senString shouldBe cPiece.senString
    board.getSquare("F1") shouldBe null
  }

  "King" should "be able to move north one Square" in  {
    val cPiece = board.getSquare("F1")
    moveData.from = "F1"
    moveData.to = "F2"
    game.move(moveData)
    board.getSquare("F2").senString shouldBe cPiece.senString
    board.getSquare("F1") should not be cPiece
  }

  "King" should "not be able to occupied square" in  {
    val cPiece = board.getSquare("F1")
    moveData.from = "F1"
    moveData.to = "G1"
    game.move(moveData)
    board.getSquare("G1") should not be cPiece
    board.getSquare("F1").senString shouldBe cPiece.senString
  }

  for (moves <- Seq(
    "B4B6___",
    "B4B5___, J12H11___, I4I5___, F12G11___, G1H2___",
    "G4G6___, F12F11___, H3E6___, H10I11___, F3H5___, H9H7___, H5H7___, L12K12___, H7H9___",
    "G4G6___, E9E7___, H3E6___, E12F11___, F3H5___, F10E9___, H5H9___, G10F10___, H9H10___, D10E8___",
    "G4G6___, A12A11___, I3H5___, A9A8___, H3E6___, C9C7___, H5F6___, K9K8___, F3H5___, F12F11___, H5H9___",
    "G4G6___, C12A11___, H3E6___, H9H7___, G6H7___, H10I11___, F3I6___, E12F11___, G10G11___, C9C7___, I6I9___, H12G11___",
    "I3H5___, G12H11___, A1A3___, H12G11___, L4L6___, C9C7___, G4G6___, L12L11___, A4A6___, C10C8___, H3D7___, I10H12___, D7E6___, H9H8___, C1E2___, E9E8___, H5I3___, F10E11___, E6F7___, C7C6___, G3F2___, C8C7___, I3G2___, J10I10___, D3C5___, J9J8___, G2I1___, C11C10___, F3G2___",
    "D4D5___, L12K12___, F4F6___, I9I7___, I4I5___, E9E8___, J4J6___, J9J7___, F1F2___, J10J9___, G4G5___, F10E9___, F2F1___, L9L7___, G3G4___, G12F11___, H4H5___, G9G8___, H1I2___, E9H6___, I5I6___, B9B7___, L4L6___, A9A8___, F3D1___, D9D8___, D1D2___, H10F8___, D3E5___, F8I11___, I3H1___, E10D9___, L1L2___, H12E9___, H3L7___, G8G7___, E3F4___, I11J10___, D2F2___, F11D11___, F2D4___, I10G9___, J3F3___, H6H5___, G4F5___, H5H3___, G1H2_F1_",
    "E4E6___, D9D7___, B4B6___, A12A10___, D3F2___, H12I11___, C4C5___, E12F11___, F3E2___, C12E11___, H1A8___, D10E8___, H3G2___, F11E12___, A4A5___, G12I12___, G1H1___, E11D9___, A1A4___, F10G11___, K4K6___, G11F11___, G2D5___, G10G11___, H1G1___, H9H8___, A8B7___, D7E6___, I4I6___, F12E11___, G3H2___, H8H7___, A5A6___, E8C7___, A4B4___, J9J8___, L4L6___, E10D11___, D5C4___, I10K11___, C4D5___, I11H12___, D5H1___, C7A6___, L1L4___, J10J9___, L4L5___, F11K6___, L5L3_F1_",
    "G3G2___, I9I8___, C1A2___, F12E11___, A4A6___, B9B7___, D3C5___, G12H11___, C5E6___, L12L11___, I4I6___, D10F11___, E6F8___, G10G11___, G2F2___, E11F12___, F2G3___, J9J8___, G3H2___, F10E11___, D4D5___, I8I7___, F3E2___, H9H8___, J4J6___, H8H7___, E2D1___, J10J9___, H1G2___, E11D12___, F1E2___, J9I9___, J6I7___, J8J7___, L1L2___, J11J10___, E3B6___, H11G10___, F4F5___, A12A10___, I3J5___, D12F10___, E2F1___, B7A6___, G4G5___, L11K11___, L2L3___, F11D10___, F8E10___, K11E11_G11_",
    "A4A5___, C9C7___, A1A3___, G12H11___, J4J5___, I10H8___, I3K2___, J10I10___, E3D2___, J11J10___, J5J6___, E10D11___, H4H5___, F12F11___, K2I1___, A12A10___, G1I3___, I9I7___, J1H2___, J9J7___, K4K6___, A10A11___, I3G5___, H10K7___, D4D6___, F10G11___, D3E5___, F11F10___, F3F2___, I10I8___, D6D7___, K7H10___, J3J5___, B9B8___, F2G2___, H10I11___, G2E2___, F9F8___, G3G2___, B10B9___, C3F3___, D11E10___, H2J1___, G11G12___, G2G3___, D10F11___, J6I7___, E10D11___, J5L5___, C10D10___, G5G9___, L12L11___"
  )) {
    "Valid move" should s"not cause exception, example 1, moves: $moves" in {
      val game = GameGenerator.loadFromSEN(GameGenerator.startingSEN)

      for (move <- moves.split(", ").toSeq) {
        // Ensure that the move is valid
        val possibleMoves = game.possibleMoves.map(_.toString)
        possibleMoves.contains(move) shouldBe true

        val moveResultOpt = game.move(new MoveData(move))
        moveResultOpt should not be None

        checkGetNextBestMoveIfGameNotOver(game, moveResultOpt.get.mateData)
      }
    }
  }

  "Valid move" should "not cause exception, example 2" in {
    val game = GameGenerator.loadFromSEN("3k5P2/3P6k1/1b10/10n1/2B9/2pN1b3P2/4n2R4/10R1/r6B4/12/5K1K4/6B5 b - - - 0 242")
    val move = "A4A2___"

    // Ensure that the move is valid
    val possibleMoves = game.possibleMoves.map(_.toString)
    possibleMoves.contains(move) shouldBe true

    val moveResultOpt = game.move(new MoveData(move))
    moveResultOpt should not be None

    checkGetNextBestMoveIfGameNotOver(game, moveResultOpt.get.mateData)
  }

  private def checkGetNextBestMoveIfGameNotOver(game: Game, mateData: MateData) {
    val gameNotOver = !mateData.trueCheckMate && !mateData.staleMate
    if (gameNotOver) game.nextBestMove should not be None
  }
}
