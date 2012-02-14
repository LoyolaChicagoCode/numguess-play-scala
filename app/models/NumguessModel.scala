package models

import scala.util.Random

case class NumguessModel(min: Int, max: Int, answer: Int = -1, numGuesses: Int = 0, comparison: Int = 0) {

  def reset(answer: Int) = NumguessModel(min, max, answer)

  def reset(): NumguessModel = reset(min + math.abs(Random.nextInt() % (max - min + 1)))

  def guess(value: Int) = NumguessModel(min, max, answer, numGuesses + 1, value - answer)
}
