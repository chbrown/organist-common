package org.anist.common

object Itertools {
  /**
    * Partition the items in list into groups such that consecutive items
    * with the same value of keyFn(item) fall into the same group.
    *
    * Why doesn't Scala infer the T & K type parameters unless keyFn is in a second argument?
    *
    * References: http://stackoverflow.com/q/4761386
    */
  def consecutiveGroupBy[T, K](xs: Iterable[T])(keyFn: T => K): Iterable[(K, List[T])] = {
    xs match {
      case head :: tail =>
        val initialAccumulator = (keyFn(head), List(head), List[(K, List[T])]())
        val (finalKey, finalGroup, allButFinalPairs) = tail.foldLeft(initialAccumulator) {
          case ((previousKey, previousGroup, allPairs), x) =>
            val key = keyFn(x)
            if (previousKey == key) {
              // prepend x to previousGroup
              (key, x :: previousGroup, allPairs)
            }
            else {
              // start a new group and prepend the previousGroup to allPairs
              val previousPair = (previousKey, previousGroup)
              (key, List(x), previousPair :: allPairs)
            }
        }
        (finalKey, finalGroup) :: allButFinalPairs
      case _ => Nil
    }
  }

  // Traversable is more general, but we might want to run .grouped subsequently
  def cross[X, Y](xs: Iterable[X], ys: Iterable[Y]) =
    for { x <- xs.view; y <- ys.view } yield (x, y)
}
