package synergychess.engine

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation._

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
case class Position() {
  var data: Game = GameGenerator.loadFromSEN(GameGenerator.startingSEN)

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

    val moveData = MoveData()
    moveData.from = from
    moveData.enPassant = data.enPassant
    moveData.castling = data.castling
    moveData.board = data.board

    val result = data.board.getSquare(from).validMoves(moveData)
    result.toJSArray
  }

  @JSExport
  def updatePosition(
    from: String,
    to: String,
    rookPlacement: String,
    promotion: String,
    kingChoice: String): js.Array[String] = {
    val moveData = MoveData()

    moveData.from = from
    moveData.to = to
    moveData.rookPlacement = rookPlacement
    moveData.enPassant = data.enPassant
    moveData.castling = data.castling
    moveData.board = data.board
    if (promotion != "") {
      moveData.promotionData = new PromotionData()
      moveData.promotionData.name = promotion
    }

    val moveResult = data.move(moveData)
    val mateData = moveResult.get.mateData

    if (mateData == null) {
      Array("", moveResult.get.toNotation(data).getNotation).toJSArray
    } else {
      Array(mateData.toString, moveResult.get.toNotation(data).getNotation).toJSArray
    }
  }

  @JSExport
  def updatePositionFromString(moveDataString: String): String = {
    val moveData = new MoveData(moveDataString)

    moveData.enPassant = data.enPassant
    moveData.castling = data.castling
    moveData.board = data.board

    val moveResult = data.move(moveData)
    val mateData = moveResult.get.mateData

    if (mateData == null) {
      ""
    } else {
      mateData.toString
    }
  }

  @JSExport
  def senString: String = {
    data.senString
  }

  @JSExport
  def piecesCaptured(color: String): js.Dictionary[Int] = {
    val captures = data.piecesCaptured(color)
    captures.toJSDictionary
  }
}

@JSExportTopLevel("MoveData")
case class MoveDataJs() {
  @JSExport
  var from: String = _
  var to: String = _
  var rookPlacement: String = _
  var kingChoice: String = _
  var promotionData: PromotionData = _

  @JSExport
  def init(
    from: String,
    to: String,
    rookPlacement: String,
    kingChoice: String,
    name: String) {
    this.from = from
    this.to = to
    this.kingChoice = kingChoice
    this.rookPlacement = rookPlacement

    if (name != "") {
      promotionData = new PromotionData()
      promotionData.name = name
    } else {
      promotionData = null
    }
  }

  @JSExport
  def getDataFromString(moveString: String): js.Array[String] = {
    val moveData = new MoveData(moveString)

    val result = Array(
      moveData.from,
      moveData.to,
      moveData.rookPlacement,
      moveData.kingChoice,
      if (moveData.promotionData != null) moveData.promotionData.name else ""
    )

    result.toJSArray
  }

  @JSExport
  override def toString: String = {
    val moveData = MoveData()
    moveData.from = from
    moveData.to = to
    moveData.rookPlacement = rookPlacement
    moveData.kingChoice = kingChoice
    moveData.promotionData = promotionData
    moveData.toString()
  }
}
