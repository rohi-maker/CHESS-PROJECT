package synergychess.engine

import scala.collection.mutable.ArrayBuffer

case class Point(var x: Int, var y: Int) {
  def this(position: String) {
    this(position.charAt(0).toInt - 64, position.substring(1, position.length).toInt)
  }

  def this(src: Point) {
    this(src.x, src.y)
  }

  def this() {
    this(0, 0)
  }

  def equals(anotherPoint: Point): Boolean = x == anotherPoint.x && y == anotherPoint.y

  def inBounds: Boolean = (0 < x && x <= 12) && (0 < y && y <= 12)

  override def toString: String = (x + 64).toChar.toString + y.toString

  def pointsBetween(end: String): ArrayBuffer[String] = {
    val between = ArrayBuffer[String]()

    val p1 = Point(x, y)
    val p2 = new Point(end)

    val xOff = if (p1.x == p2.x) 0 else if (p2.x > p1.x) 1 else -1
    val yOff = if (p1.y == p2.y) 0 else if (p2.y > p1.y) 1 else -1

    while (p1.toString != p2.toString) {
      p1.x += xOff
      p1.y += yOff
      between.append(p1.toString)
    }
    between
  }
}
