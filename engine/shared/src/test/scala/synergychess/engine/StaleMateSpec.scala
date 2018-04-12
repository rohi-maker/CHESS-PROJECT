package synergychess.engine

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class StaleMateSpec extends FlatSpec with Matchers with BeforeAndAfter  {
  var game: Game = Game()
  var board: Board = new Board()
  var moveInfo: MoveData = new MoveData()

  before {
    val cSen = "KP10/PP10/12/12/5p6/5p6/6P5/12/12/12/pp10/kp10 w KQkq KQkq - 0 0"
    game = GameGenerator.loadFromSEN(cSen)
    board = game.board
    moveInfo = new MoveData()
    moveInfo.board = board
    moveInfo.castling = new Castling()
  }

  "" should "be stalemate case 1" in {
    moveInfo.from = "G6"
    moveInfo.to = "F7"
    val result = game.move(moveInfo)

    result.mateData.staleMate = true
    result.mateData.checkMate = false
  }

  "" should "not be stalemate" in {
    moveInfo.from = "G6"
    moveInfo.to = "G7"
    val result = game.move(moveInfo)

    result.mateData.staleMate = false
    result.mateData.checkMate = false
  }

  "" should "be stalemate case 2" in {
    moveInfo.from = "G6"
    moveInfo.to = "G7"
    game.move(moveInfo)

    moveInfo.from = "F8"
    moveInfo.to = "G7"
    val result = game.move(moveInfo)

    result.mateData.staleMate = true
    result.mateData.checkMate = false
  }
}
