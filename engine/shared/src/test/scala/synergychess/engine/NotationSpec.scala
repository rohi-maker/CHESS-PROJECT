package synergychess.engine

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class NotationSpec extends FlatSpec with Matchers with BeforeAndAfter {
  var startingSEN = "r1n1bkqb1n1r/2p6p2/1prnbqkbnrp1/pppppppppppp/12/12/12/12/PPPPPPPPPPPP/1PRNBQKBNRP1/2P6P2/R1N1BKQB1N1R w KQkq KQkq - 0 0"
  var game: Game = Game()
  var board: Board = new Board()
  var moveData: MoveData = MoveData()
  var notation: Notation = Notation()

  before {
    notation.resetNotation()
  }

  "" should "be able to convert piece names to characters" in {
    notation.pChar("Queen") shouldBe "Q"
    notation.pChar("Rook") shouldBe "R"
    notation.pChar("King") shouldBe "K"
    notation.pChar("Knight") shouldBe "N"
    notation.pChar("Bishop") shouldBe "B"
    notation.pChar("Pawn") shouldBe ""
    notation.pChar("Infantry") shouldBe ""
  }

  "" should "return correct notation for pawns" in {
    notation.moveFrom = "f4"
    notation.moveTo = "f5"
    notation.attPiece = "pawn"
    notation.piece = ""

    notation.getNotation shouldBe "f5"
  }

  "" should "return correct notation for major pieces" in {
    notation.moveFrom = "l1"
    notation.moveTo = "l3"
    notation.piece = "R"

    notation.getNotation shouldBe "Rl3"
  }

  "" should " for a pawn" in {
    notation.moveFrom = "h4"
    notation.moveTo = "i5"
    notation.piece = ""  // pawn
    notation.isTaken = true

    notation.getNotation shouldBe "hxi5"
  }

  "" should " for a major piece" in {
    notation.moveFrom = "l1"
    notation.moveTo = "l3"
    notation.piece = "R"
    notation.isTaken = true

    notation.getNotation shouldBe "Rxl3"
  }

  "" should "return correct notation for first double square move" in {
    notation.moveFrom = "f4"
    notation.moveTo = "f6"
    notation.attPiece = "pawn"
    notation.piece = ""

    notation.getNotation shouldBe "f6"
  }

  "" should "return correct notation for enpassant taking" in {
    notation.moveFrom = "f7"
    notation.moveTo = "e8"
    notation.piece = ""
    notation.attPiece = "pawn"
    notation.isTaken = true

    notation.getNotation shouldBe "fxe8"
  }

  "" should "return correct notation for pawns promotion" in {
    notation.moveFrom = "e11"
    notation.moveTo = "d12"
    notation.promoPiece = "Q"
    notation.attPiece = "pawn"
    notation.isPromotion = true

    notation.getNotation shouldBe "d12=Q"
  }

  "" should "for double threat moves same rank case 1" in {
    notation.moveFrom = "J5"
    notation.moveTo = "G5"
    notation.fileNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "R"

    notation.getNotation shouldBe "Rjg5"
  }

  "" should "for double threat moves same file case 1" in {
    notation.moveFrom = "G5"
    notation.moveTo = "G6"
    notation.rankNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "R"

    notation.getNotation shouldBe "R5g6"
  }

  "" should "for double threat moves, different rank and file (intersect) case 1" in {
    notation.moveFrom = "C5"
    notation.moveTo = "G5"
    notation.rankNeeded = true
    notation.fileNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "R"

    notation.getNotation shouldBe "Rc5g5"
  }

  "" should "for double threat moves same rank case 2" in {
    // e.g SEN
    // r1n1bkqb3r/2p6p2/1prnbqkbnrp1/pppppppppp1p/8n1i1/12/12/8N1I1/PPPPPPPPPP1P/1PRNBQKBNRP1/2P6P2/R1N1BKQB3R w KQkq KQkq - 0 4    
    // then KI3 > J5
    notation.moveFrom = "I3"
    notation.moveTo = "J5"
    notation.fileNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "K"

    notation.getNotation shouldBe "Kij5"
  }

  "" should "for double threat moves same file case 2" in {
    // e.g SEN
    // r1n1bkqb3r/2p6p2/1prnbqkbnrn1/pppppppppp1p/10i1/10i1/10I1/10I1/PPPPPPPPPP1P/1PRNBQKBNRN1/2P6P2/R1N1BKQB3R w KQkq KQkq - 0 3
    // then KI3 > K4
    notation.moveFrom = "I3"
    notation.moveTo = "K4"
    notation.rankNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "K"

    notation.getNotation shouldBe "K3k4"
  }

  "" should "for double threat moves, different rank and file (intersect) case 2" in {
    // e.g. SEN
    // r1n1bkqb3r/2p6p2/1prnbqkb1rp1/ppppppppppnp/10i1/6n5/6N5/10I1/PPPPPPPPPPNP/1PRNBQKB1RP1/2P6P2/R1N1BKQB3R w KQkq KQkq - 0 6
    // then KG6 > I5
    notation.moveFrom = "G6"
    notation.moveTo = "I5"
    notation.rankNeeded = true
    notation.fileNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "K"

    notation.getNotation shouldBe "Kg6i5"
  }

  "" should "for double threat moves same rank" in {
    notation.moveFrom = "i2"
    notation.moveTo = "h3"
    notation.fileNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "B"

    notation.getNotation shouldBe "Bih3"
  }

  "" should "for double threat moves same file" in {
    notation.moveFrom = "h3"
    notation.moveTo = "g2"
    notation.rankNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "B"

    notation.getNotation shouldBe "B3g2"
  }

  "" should "for double threat moves, different rank and file (intersect) case 3" in {
    notation.moveFrom = "f5"
    notation.moveTo = "i2"
    notation.rankNeeded = true
    notation.fileNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "B"

    notation.getNotation shouldBe "Bf5i2"
  }

  "" should "for double threat moves from a different rank and file" in {
    notation.moveFrom = "F3"
    notation.moveTo = "G2"
    notation.fileNeeded = true
    notation.rankNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "Q"

    notation.getNotation shouldBe "Qf3g2"
  }

  "" should "for double threat moves from the same rank case 1" in {
    // e.g.
    // r1n1bk1b1n1r/2p1q1q2p2/1prnb1kbnrp1/pppppppppppp/12/12/12/12/PPPPPPPPPPPP/1PRNB1KBNRP1/2P2Q1Q1P2/R1N1BK1B1N1R w KQkq KQkq - 0 2
    notation.moveFrom = "F3"
    notation.moveTo = "G2"
    notation.fileNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "Q"

    notation.getNotation shouldBe "Qfg2"
  }

  "" should "for double threat moves from the same file case 1" in {
    // e.g.
    // r1n1bkqb1n1r/2p4k1p2/1prnb1qbnrp1/pppppppppppp/12/12/12/12/PPPPPPPPPPPP/1PRNB1QBNRP1/2P4K1P2/R1N1BKQB1N1R w KQkq KQ - 0 2      
    notation.moveFrom = "G1"
    notation.moveTo = "F2"
    notation.rankNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "Q"

    notation.getNotation shouldBe "Q1f2"
  }

  "" should "for double threat moves from different rank and file" in {
    // e.g. KG1 -> G2 from starting board
    notation.moveFrom = "G1"
    notation.moveTo = "G2"
    notation.fileNeeded = true
    notation.rankNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "K"

    notation.getNotation shouldBe "Kg1g2"
  }

  "" should "for double threat moves from the same rank case 2" in {
    // e.g. SEN
    // r1n1b1qb1n1r/2p2k1k1p2/1prnbq1bnrp1/pppppppppppp/12/12/12/12/PPPPPPPPPPPP/1PRNBQ1BNRP1/2P2K1K1P2/R1N1B1QB1N1R w   - 0 2
    // then KF2 -> G3
    notation.moveFrom = "F2"
    notation.moveTo = "G3"
    notation.fileNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "K"

    notation.getNotation shouldBe "Kfg3"
  }

  "" should "for double threat moves from the same file case 2" in {
    // e.g. SEN
    // r1n1b1kb1n1r/2p2q3p2/1prnbqkbnrp1/pppppppppppp/12/12/12/12/PPPPPPPPPPPP/1PRNBQKBNRP1/2P4Q1P2/R1N1B1KB1N1R w kq kq - 0 2
    // then KG1 -> F2
    notation.moveFrom = "G1"
    notation.moveTo = "F2"
    notation.rankNeeded = true
    notation.isDoubleThreat = true
    notation.piece = "K"

    notation.getNotation shouldBe "K1f2"
  }

  "" should "back rank King side castling  K>b1  R>e1 = O-B-E" in {

    notation.moveFrom = "F1"
    notation.moveTo = "B1"
    notation.isOuterKingCastle = true
    notation.kingPos = "B1"
    notation.rookLocation = "E1"
    notation.piece = "K"

    notation.getNotation shouldBe "O-B-E"
  }

  "" should "back rank Queen side castling K>k1 R>i1 = O-K-I" in {
    notation.moveFrom = "F1"
    notation.moveTo = "K1"
    notation.isOuterKingCastle = true
    notation.kingPos = "K1"
    notation.rookLocation = "I1"
    notation.piece = "K"

    notation.getNotation shouldBe "O-K-I"
  }

  "" should "inner rank Queen side castling = O-O-O" in {
    notation.moveFrom = "G3"
    notation.moveTo = "E3"
    notation.isInnerQueenCastle = true
    notation.piece = "K"

    notation.getNotation shouldBe "O-O-O"
  }

  "" should "inner rank king side castling = O-O" in {
    notation.moveFrom = "G3"
    notation.moveTo = "I3"
    notation.isInnerKingCastle = true
    notation.piece = "K"

    notation.getNotation shouldBe "O-O"
  }

  "" should "puts a King in check" in {
    notation.moveFrom = "G3"
    notation.moveTo = "E3"
    notation.isInCheck = true
    notation.piece = "B"

    notation.getNotation shouldBe "Be3+"
  }

  "" should "puts two Kings in check" in {
    notation.moveFrom = "G3"
    notation.moveTo = "E3"
    notation.isInDoubleCheck = true
    notation.piece = "B"

    notation.getNotation shouldBe "Be3++"
  }

  "" should "puts a King in checkmate" in {
    notation.moveFrom = "G3"
    notation.moveTo = "E3"
    notation.isCheckmate = true
    notation.piece = "B"

    notation.getNotation shouldBe "Be3#"
  }

  "" should "puts both Kings in checkmate" in {
    notation.moveFrom = "G3"
    notation.moveTo = "E3"
    notation.isDoubleCheckMate = true
    notation.piece = "B"

    notation.getNotation shouldBe "Be3##"
  }
}
