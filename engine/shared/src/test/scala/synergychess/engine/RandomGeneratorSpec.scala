package synergychess.engine

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class RandomGeneratorSpec extends FlatSpec with Matchers with BeforeAndAfter {
  val castlingConfig = "r1n1bk5r/1npb1qqnbp2/1pr3kbnrp1/pppppppppppp/12/12/11P/12/PPPPPPPPPPP1/1PR3K2RP1/2P1NBB2PN1/R4K5R w KQkq KQkq - 0 6"

  var game: Game = Game()

  before {
    game = GameGenerator.loadFromSEN(castlingConfig)
  }

  "Generator" should "generate a valid move" in {
    val moveData = game.nextBestMove

    moveData should not be None
    game.move(moveData.get) should not be None
  }
}

