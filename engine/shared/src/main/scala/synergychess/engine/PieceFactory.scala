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
}
