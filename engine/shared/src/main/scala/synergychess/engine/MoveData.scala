package synergychess.engine

import scala.collection.mutable.ArrayBuffer

case class MoveData (
  var board: Board = new Board(),
  var enPassant: String = "",
  var from: String = "",
  var to: String = "",
  var moveList: ArrayBuffer[String] = ArrayBuffer[String](),
  var castling: Castling = new Castling(),
  var rookPlacement: String = "",
  var kingChoice: String = "",
  var promotionData: PromotionData = null
) {
  def this(another: MoveData) {
    this(
      another.board,
      another.enPassant,
      another.from,
      another.to,
      another.moveList,
      another.castling,
      another.rookPlacement,
      another.kingChoice,
      another.promotionData
    )
  }

  def this(moveDataString: String) {
    this()

    var start = 0
    if ('A' <= moveDataString(start + 2) && moveDataString(start + 2) <= 'Z') {
      from = moveDataString.substring(start, start + 2)
      start = start + 2
    } else {
      from = moveDataString.substring(start, start + 3)
      start = start + 3
    }

    if (('A' <= moveDataString(start + 2) && moveDataString(start + 2) <= 'Z') || moveDataString(start + 2) == '_') {
      to = moveDataString.substring(start, start + 2)
      start = start + 2
    } else {
      to = moveDataString.substring(start, start + 3)
      start = start + 3
    }

    if (moveDataString(start) == '_') {
      rookPlacement = ""
      start = start + 1
    } else if (('A' <= moveDataString(start + 2) && moveDataString(start + 2) <= 'Z') || moveDataString(start + 2) == '_') {
      rookPlacement = moveDataString.substring(start, start + 2)
      start = start + 2
    } else {
      rookPlacement = moveDataString.substring(start, start + 3)
      start = start + 3
    }

    if (moveDataString(start) == '_') {
      kingChoice = ""
      start = start + 1
    } else if (('A' <= moveDataString(start + 2) && moveDataString(start + 2) <= 'Z') || moveDataString(start + 2) == '_') {
      kingChoice = moveDataString.substring(start, start + 2)
      start = start + 2
    } else {
      kingChoice = moveDataString.substring(start, start + 3)
      start = start + 3
    }

    promotionData = new PromotionData()
    if (moveDataString(start) == '_') {
      promotionData = null
    } else {
      promotionData.name = moveDataString(start) match {
        case 'r' => "rook"
        case 'b' => "bishop"
        case 'q' => "queen"
        case 'n' => "knight"
      }
    }
  }

  override def toString: String = {
    from + to +
      (if (rookPlacement != "") rookPlacement else "_") +
      (if (kingChoice != "") kingChoice else "_") +
      (if (promotionData != null) promotionData.name(0) else "_")
  }
}
