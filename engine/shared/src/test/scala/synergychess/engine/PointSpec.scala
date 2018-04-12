package synergychess.engine

import org.scalatest._

class PointSpec extends FlatSpec with Matchers {
  val castlingConfig = "r1n1bk5r/1npb1qqnbp2/1pr3kbnrp1/pppppppppppp/12/12/11P/12/PPPPPPPPPPP1/1PR3K2RP1/2P1NBB2PN1/R4K5R w KQkq KQkq - 0 6"
  "inBounds" should "returns true for (9, 9)" in {
    val point = Point(9, 9)
    point.inBounds shouldBe true
  }
}
