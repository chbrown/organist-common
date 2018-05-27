package org.anist

package object stats {
  /**
    * Find the average of a series of numbers, whether they're decimals or integrals.
    *
    * Uses the solution from http://stackoverflow.com/a/6190665
    *
    * @param xs Series of numbers (with .sum and .size values)
    * @return A number with the same type as the input values
    */
  def mean[T : Numeric](xs: TraversableOnce[T]): T = implicitly[Numeric[T]] match {
    case num: Fractional[_] =>
      import num._
      xs.sum / fromInt(xs.size)
    case num: Integral[_] =>
      import num._
      xs.sum / fromInt(xs.size)
    case num =>
      sys.error(s"Indivisable numeric: $num")
  }
}
