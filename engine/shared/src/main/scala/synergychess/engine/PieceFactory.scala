package synergychess.engine

object PieceFactory {
  def newPiece(name: String, color: String, basePos: Point): Piece = name match {
    case "pawn" => new Pawn(color, basePos)
    case "rook" => new Rook(color, basePos)
    case "bishop" => new Bishop(color, basePos)
    case "queen" => new Queen(color, basePos)
    case "knight" => new Knight(color, basePos)
    case "king" => new King(color, basePos)
    // A pawn that can't double jump
    case "infantry" => new Pawn(color, basePos, false)
  }

  def newPiece(pChar: Char, basePos: Point): Piece = {
    newPiece(getPieceName(pChar), getPieceTeam(pChar), basePos)
  }

  private def getPieceName(pChar: Char): String = {
    pChar.toUpper match {
      case 'P' =>  "pawn"
      case 'N' =>  "knight"
      case 'B' =>  "bishop"
      case 'R' =>  "rook"
      case 'Q' =>  "queen"
      case 'K' =>  "king"
      case 'I' =>  "infantry"
    }
  }

  private def getPieceTeam(pChar: Char): String = {
    if (pChar.isUpper) "white" else "black"
  }
}
