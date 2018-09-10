package synergychess.engine

case class Notation (
  var piece: String = "",     // Get piece
  var moveFrom: String = "",  // Board ref e.g J2
  var moveTo: String = "",    // Board ref e.g.j2

  var kingPos: String = "",
  var kingRemoved: String = "",
  var rookLocation: String = "",

  var isTaken: Boolean = false,
  var enPassant: Boolean = false,
  var attPiece: String = "", //attacking piece
  var isDoubleThreat: Boolean = false,
  var rankNeeded: Boolean = false,
  var fileNeeded: Boolean = false,

  var isInCheck: Boolean = false,
  var isInDoubleCheck: Boolean = false,
  var isCheckmate: Boolean = false,
  var isDoubleCheckMate: Boolean = false,

  var isInnerKingCastle: Boolean = false,
  var isInnerQueenCastle: Boolean = false,
  var isOuterKingCastle: Boolean = false,
  var isOuterQueenCastle: Boolean = false,

  var isPromotion: Boolean = false,
  var promoPiece: String = ""
) {
  def resetNotation() {
    moveFrom = ""
    moveTo = ""
    piece = ""

    kingPos = ""
    kingRemoved = ""
    rookLocation = ""

    isTaken = false
    isDoubleThreat = false
    rankNeeded = false
    fileNeeded = false
    attPiece = ""

    isInCheck = false
    isInDoubleCheck = false
    isCheckmate = false
    isDoubleCheckMate = false

    isInnerKingCastle = false
    isInnerQueenCastle = false
    isOuterKingCastle = false
    isOuterQueenCastle = false

    isPromotion = false
    promoPiece = ""
  }

  def castleWhichWay(moveData: MoveData) {
    val kingsBase = Array("F1", "G3", "G10", "F12")
    val outerKingside = Array("A1", "B1", "C1", "D1", "E1", "A12", "B12", "C12", "D12", "E12")
    val outerQueenside = Array("G1", "H1", "I1", "J1", "K1", "L1", "G12", "H12", "I12", "J12", "K12", "L12")
    val innerKingside = Array("I3", "I10")
    val innerQueenside = Array("E3", "E10")

    // It's a king starting kings base position and is to a king side castling move target
    if ((kingsBase.indexOf(moveData.from) != -1) && (outerKingside.indexOf(moveData.to) != -1) && moveData.rookPlacement != "") {
      kingPos = moveData.to
      rookLocation = moveData.rookPlacement
      isOuterKingCastle = true
      isOuterQueenCastle = false //there can be only one
    }
    // If to is one of outer Queen side AND piece is King (K)
    if ((kingsBase.indexOf(moveData.from) != -1) && (outerQueenside.indexOf(moveData.to) != -1) && moveData.rookPlacement != "") {
      kingPos = moveData.to
      rookLocation = moveData.rookPlacement
      isOuterQueenCastle = true
      isOuterKingCastle = false //there can be only one
    }
    // Inner king side castle
    if ((kingsBase.indexOf(moveData.from) != -1) && (innerKingside.indexOf(moveData.to) != -1)) {
      kingPos = moveData.to
      rookLocation = moveData.rookPlacement
      isInnerKingCastle = true
      isInnerQueenCastle = false //there can be only one
    }
    // Inner queen side castle
    if ((kingsBase.indexOf(moveData.from) != -1) && (innerQueenside.indexOf(moveData.to) != -1)) {
      kingPos = moveData.to
      rookLocation = moveData.rookPlacement
      isInnerQueenCastle = true
      isInnerKingCastle = false //there can be only one
    }
  }

  def pChar(pieceName: String): String = {
    val piece = pieceName.toLowerCase
    piece match {
      case "pawn" => ""
      case "knight" => "N"
      case "bishop" => "B"
      case "rook" => "R"
      case "queen" => "Q"
      case "king" => "K"
      case "infantry" => ""
    }
  }

  def reversePChar(pieceName: String): String = {
    piece match {
      case "" => "pawn"
      case "N" => "knight"
      case "B" => "bishop"
      case "R" => "rook"
      case "Q" => "queen"
      case "K" => "king"
    }
  }

  def getNotation: String = {
    var notationString = ""

    if (piece == "") {
      piece = "pawn"
    } else if (piece.charAt(0).toUpper == piece.charAt(0)) {
      piece = reversePChar(piece)
    }

    if (isPromotion && promoPiece.charAt(0).toUpper != promoPiece.charAt(0)) {
      promoPiece = reversePChar(promoPiece)
    }

    // TODO check for charAt() (10,11,12)
    // Originating rank and file, no ambiguity
    var rankFrom = ""
    var fileFrom = ""

    if (moveFrom != null && moveFrom != "") {  // During back rank castle
      rankFrom = if (moveFrom.length > 2) moveFrom.substring(1, 3) else moveFrom.charAt(1).toString
      fileFrom = moveFrom.charAt(0).toString.toLowerCase
    }

    // Construct the mainNotation
    var mainNotation = ""
    if (isPromotion)
      mainNotation = moveTo.toLowerCase + "=" + promoPiece
    else
      mainNotation = pChar(piece) +
        (if (isTaken) if (piece == "pawn") fileFrom + "x" else "x" else "") +
        moveTo.toLowerCase +
        (if (enPassant) "e.p." else "")

    if (isDoubleThreat) { //threatened by another of the same type of piece
      mainNotation = pChar(piece) + (if (fileNeeded) fileFrom else "") //so need file or rank extra inf
      mainNotation += (if (rankNeeded) rankFrom else "")
      mainNotation += (if (isTaken) "x" else "") + moveTo.toLowerCase()
    }

    // Normal moves or Promotion, not castling
    if (!isInnerKingCastle && !isInnerQueenCastle && !isOuterKingCastle && !isOuterQueenCastle) {
      if (isDoubleCheckMate) {
        notationString = mainNotation + "##"
      } else {
        if (isCheckmate) {
          notationString = mainNotation + "#"
        } else {
          notationString = mainNotation + (if (isInCheck) "+" else "")
          notationString += (if (isInDoubleCheck) "++" else "")
          notationString = (if (kingRemoved != "") "x" + kingRemoved.toLowerCase + ", " else "") + notationString
        }
      }
    } else { // Castling
      if (isInnerKingCastle || isInnerQueenCastle || isOuterKingCastle || isOuterQueenCastle) {
        var rookLoc = ""
        // Munge the rook locations for single step king back Rank castle vs, multi step back Rank castle
        if (rookLocation != "") rookLoc = rookLocation.charAt(0).toString
        else {
          if (isInnerKingCastle) rookLoc = "H"

          if (isInnerQueenCastle) rookLoc = "F"
        }
        // Same for either team
        notationString = if (isInnerKingCastle) "O-O" else ""
        notationString += (if (isInnerQueenCastle) "O-O-O" else "")
        notationString += (if (isOuterKingCastle) "O-" + kingPos.charAt(0) + "-" + rookLoc else "")
        notationString += (if (isOuterQueenCastle) "O-" + kingPos.charAt(0) + "-" + rookLoc else "")

        // feasible rook can put opp King in check after castling + (if (isInDoubleCheck)  { "++"}
        notationString += (if (isInCheck) "+" else "")

        notationString += (if (isInDoubleCheck) "++" else "")
        notationString += (if (isCheckmate) "#" else "")
        notationString += (if (isDoubleCheckMate) "##" else "")
      }
    }
    notationString
  }
}
