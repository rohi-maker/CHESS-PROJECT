package synergychess.engine

import org.scalatest.{FlatSpec, Matchers}

class deserializerSpec extends FlatSpec with Matchers {
  "MoveData" should "deserialize correctly" in {
    val moveData = new MoveData("A1B11___")

    moveData.from shouldBe "A1"
    moveData.to shouldBe "B11"
  }

  "MoveData" should "deserialize correctly in case of castling" in {
    val moveData = new MoveData("A11B1C11__")

    moveData.from shouldBe "A11"
    moveData.to shouldBe "B1"
    moveData.rookPlacement shouldBe "C11"
  }

  "MoveData" should "deserialize correctly in case of king remove" in {
    val moveData = new MoveData("A1B1_F9_")

    moveData.from shouldBe "A1"
    moveData.to shouldBe "B1"
    moveData.kingChoice shouldBe "F9"
  }

  "MoveData" should "deserialize correctly in case of promoting" in {
    val moveData = new MoveData("A11B11__q")

    moveData.from shouldBe "A11"
    moveData.to shouldBe "B11"
    moveData.promotionData.name shouldBe "queen"
  }
}
