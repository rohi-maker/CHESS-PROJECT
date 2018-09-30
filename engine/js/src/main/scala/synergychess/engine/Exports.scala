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
  private var game: Game = GameGenerator.loadFromSEN(GameGenerator.startingSEN)

  @JSExport
  def setSEN(sen: String) {
    game = GameGenerator.loadFromSEN(sen)
  }

  @JSExport
  def setSquare(col: Int, rank: Int, piece: String) {
    val pos = Point(col, rank)
    game.board.setSquare(Point(col, rank), PieceFactory.newPiece(piece(0), pos))
  }

  @JSExport
  def removeSquare(col: Int, rank: Int) {
    game.board.removeSquare(Point(col, rank))
  }

  @JSExport
  def getTeamToMove: String = {
    game.teamToMove
  }

  @JSExport
  def validMoves(from: String): js.Array[String] = {
    if (game.board.getSquare(from) == null || game.board.getSquare(from).color != game.teamToMove) {
      return js.Array[String]()
    }

    val moveData = MoveData()
    moveData.from = from
    moveData.enPassant = game.enPassant
    moveData.castling = game.castling
    moveData.board = game.board

    val result = game.board.getSquare(from).validMoves(moveData)
    result.toJSArray
  }

  @JSExport
  def castlings: js.Dictionary[Int] = {
    val cas = new Castling()
    cas.status = scala.collection.mutable.Map(
      "F1"  -> Array(true, false),
      "G3"  -> Array(true, true),
      "G10" -> Array(false, true),
      "F12" -> Array(true, true)
    )
    val castlings = cas.status.map {
      e => (e._1, (if (e._2(0)) 1 else 0) + (if (e._2(1)) 2 else 0))
    }
    castlings.toJSDictionary
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
    moveData.enPassant = game.enPassant
    moveData.castling = game.castling
    moveData.board = game.board
    if (promotion != "") {
      moveData.promotionData = new PromotionData()
      moveData.promotionData.name = promotion
    }

    val moveResult = game.move(moveData)
    val mateData = moveResult.get.mateData

    if (mateData == null) {
      Array("", moveResult.get.toNotation(game).getNotation).toJSArray
    } else {
      Array(mateData.toString, moveResult.get.toNotation(game).getNotation).toJSArray
    }
  }

  @JSExport
  def updatePositionFromString(moveDataString: String): js.Array[String] = {
    val moveData = new MoveData(moveDataString)

    moveData.enPassant = game.enPassant
    moveData.castling = game.castling
    moveData.board = game.board

    val moveResult = game.move(moveData)
    val mateData = moveResult.get.mateData

    if (mateData == null) {
      Array("", moveResult.get.toNotation(game).getNotation).toJSArray
    } else {
      Array(mateData.toString, moveResult.get.toNotation(game).getNotation).toJSArray
    }
  }

  @JSExport
  def color: String = {
    game.teamToMove
  }

  @JSExport
  def senString: String = {
    game.senString
  }

  @JSExport
  def piecesCaptured(color: String): js.Dictionary[Int] = {
    val captures = game.piecesCaptured(color)
    captures.toJSDictionary
  }

  @JSExport
  def nextBestMove(level: Int): String = {
    game.nextBestMove(level) match {
      case None => ""
      case Some(moveData) => moveData.toString
    }
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
