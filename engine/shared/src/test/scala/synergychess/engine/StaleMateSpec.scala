package synergychess.engine

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class StaleMateSpec extends FlatSpec with Matchers with BeforeAndAfter  {
  var game: Game = Game()
  var board: Board = new Board()
  var moveData: MoveData = new MoveData()

  before {
    val cSen = "KP10/PP10/12/12/5p6/5p6/6P5/12/12/12/pp10/kp10 w KQkq KQkq - 0 0"
    game = GameGenerator.loadFromSEN(cSen)
    board = game.board
    moveData = new MoveData()
    moveData.board = board
    moveData.castling = new Castling()
  }

  "" should "be stalemate case 1" in {
    moveData.from = "G6"
    moveData.to = "F7"
    val result = game.move(moveData)

    result.get.mateData.staleMate shouldBe true
    result.get.mateData.checkMate shouldBe false
  }

  "" should "not be stalemate" in {
    moveData.from = "G6"
    moveData.to = "G7"
    val result = game.move(moveData)

    result.get.mateData.staleMate shouldBe false
    result.get.mateData.checkMate shouldBe false
  }

  "" should "be stalemate case 2" in {
    moveData.from = "G6"
    moveData.to = "G7"
    game.move(moveData)

    moveData.from = "F8"
    moveData.to = "G7"
    val result = game.move(moveData)

    result.get.mateData.staleMate shouldBe true
    result.get.mateData.checkMate shouldBe false
  }
}
