package synergychess.engine

import scala.scalajs.js
import scala.scalajs.js.annotation._
import js.JSConverters._
import scala.collection.mutable.ArrayBuffer

@JSExportTopLevel("GameGenerator")
object GameGeneratorJs {
  @JSExport
  def loadFromSEN(senString: String): js.Array[js.Array[String]] = {
    val result = Array.ofDim[String](12, 12)
    val board = GameGenerator.loadFromSEN(senString).board.gameBoard
    for ((position, element) <- board) {
      val pos = getPosition(position)
      result(pos(0))(pos(1)) = if (element == null) "" else element.senString
    }
    result.map(_.toJSArray).toJSArray
  }

  def getPosition(position: String): Array[Int] = {
    val result = new Array[Int](2)
    result(1) = position(0).toInt - 65
    if (position.length == 2) result(0) = position(1).toInt - 48 - 1
    else result(0) = (position(1).toInt - 48) * 10 + position(2).toInt - 48 - 1
    result
  }
}

@JSExportTopLevel("Position")
class Position {
  val startingSEN = "r1n1bkqb1n1r/2p6p2/1prnbqkbnrp1/pppppppppppp/12/12/12/12/PPPPPPPPPPPP/1PRNBQKBNRP1/2P6P2/R1N1BKQB1N1R w KQkq KQkq - 0 0"
  var data: Game = GameGenerator.loadFromSEN(startingSEN)

  @JSExport
  def setSEN(sen: String) {
    data = GameGenerator.loadFromSEN(sen)
  }

  @JSExport
  def getTeamToMove: String = {
    data.teamToMove
  }

  @JSExport
  def validMoves(from: String): js.Array[String] = {
    if (data.board.getSquare(from) == null || data.board.getSquare(from).color != data.teamToMove) {
      return js.Array[String]()
    }

    val moveInfo = new MoveData()
    moveInfo.from = from
    moveInfo.enPassant = data.enPassant
    moveInfo.castling = data.castling
    moveInfo.board = data.board

    val result = data.board.getSquare(from).validMoves(moveInfo)
    result.toJSArray
  }

  @JSExport
  def updatePosition(from: String, to: String, rookPlacement: String, promotion: String, kingChoice: String): String = {
    val moveInfo = new MoveData()

    if (kingChoice != "") {
      moveInfo.from = kingChoice

      val moveResult = data.move(moveInfo)
      val mateData = moveResult.mateData

      if (mateData == null) {
        data.removedKingChoices = ArrayBuffer[String]()
      } else {
        data.removedKingChoices = mateData.choices
      }
    }

    moveInfo.from = from
    moveInfo.to = to
    moveInfo.rookPlacement = rookPlacement
    moveInfo.enPassant = data.enPassant
    moveInfo.castling = data.castling
    moveInfo.board = data.board
    if (promotion != "") {
      moveInfo.promotionData = new PromotionData()
      moveInfo.promotionData.name = promotion
    }

    val moveResult = data.move(moveInfo)
    val mateData = moveResult.mateData

    if (mateData == null) {
      data.removedKingChoices = ArrayBuffer[String]()
      ""
    } else {
      data.removedKingChoices = mateData.choices
      mateData.toString
    }
  }

  @JSExport
  def senString: String = {
    data.senString
  }
}
