package org.anist.common

import scala.collection.mutable

package object iter {
  implicit class IterableOps[+A](val iterable: Iterable[A]) extends AnyVal {
    def countBy[K](keyFn: (A) => K): Map[K, Int] = {
      // TODO: optimize to avoid groupBy middleman
      iterable.groupBy(keyFn).map(kRepr => (kRepr._1, kRepr._2.size))
    }

    /**
      * Partition the items in list into groups such that consecutive items
      * with the same value of keyFn(item) fall into the same group.
      *
      * Why doesn't Scala infer the T & K type parameters unless keyFn is in a second argument?
      *
      * References: http://stackoverflow.com/q/4761386
      */
    def consecutiveGroupBy[K](keyFn: (A) => K): Iterable[(K, List[A])] = {
      iterable match {
        case head :: tail =>
          val initialAccumulator = (keyFn(head), List(head), List[(K, List[A])]())
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
  }

  /**
    * @param iterator The underlying Iterator, such as the result of Source.getLines()
    * @tparam A The type of the elements in the underlying Iterator.
    */
  implicit class IteratorOps[+A](val iterator: Iterator[A]) extends AnyVal {
    /**
    * Inspired by http://stackoverflow.com/a/10643762, but:
    * - includes the keys in the resulting Iterator
    * - is as lazy as possible, instead of collecting each sub-Iterator into a List
    *
    * N.b. It will probably throw java.lang.StackOverflowError.
    *   See https://groups.google.com/forum/#!topic/scala-user/BPjFbrglfMs for details.
    *
    * You should probably use StrictGroupByIterator instead.
    *
    * @param keyFn The discriminator function. As the input is grouped into sub-iterators
    *              such that for all items in the iterator, keyFn(item) returns the same value
    */
    def lazyGroupBy[K](keyFn: (A) => K) = new Iterator[(K, Iterator[A])] {
      // cursor must be private, otherwise it doesn't type-check!
      private var cursor = iterator
      // don't touch iterator after this!

      def hasNext = cursor.hasNext

      def next: (K, Iterator[A]) = {
        val currentHead = cursor.next()
        val currentKey = keyFn(currentHead)
        // split the cursor into things
        val (currentTail, nextCursor) = cursor.span(item => keyFn(item) == currentKey)
        // don't touch cursor after this!
        // ...until we set it to something else, like nextCursor:
        cursor = nextCursor
        // return the (key, iterator) tuple
        (currentKey, Iterator.single(currentHead) ++ currentTail)
      }
    }

    /**
      * Nested Iterators (see LazyGroupByIterator) are a mess. If we have lazy nested iterators,
      * what if we call next() on the outer iterator before the inner one? And in most cases, it's the
      * outer Iterator that's important.
      *
      * Initialization is non-lazy; iterator.next() will be called immediately to prepare the internal buffer
      *
      * @param keyFn The function that distinguishes between groups.
      * @tparam K The type of the keys derived from each element via keyFn.
      */
    def strictGroupBy[K](keyFn: (A) => K) = new Iterator[(K, Seq[A])] {
      // bufKey and buf should either be both null or both not-null
      // but since K might be a String, which cannot be null, we have to wrap in Options and use None instead of null
      private var buf: Option[A] = None
      private var bufKey: Option[K] = None
      if (iterator.hasNext) {
        val item = iterator.next()
        buf = Some(item)
        bufKey = Some(keyFn(item))
      }

      def hasNext = bufKey.nonEmpty

      def next: (K, Seq[A]) = {
        // bufKey and buf should be set
        val currentKey = bufKey.get
        val currentSeq = new mutable.Queue[A]
        currentSeq += buf.get
        //val currentHead = iterator.next()
        //val currentKey = keyFn(currentHead)
        // split the cursor into things
        while (iterator.hasNext) {
          val item = iterator.next()
          val itemKey = keyFn(item)
          if (itemKey == currentKey) {
            // keep going
            currentSeq += item
          }
          else {
            // buffer the current item and return
            buf = Some(item)
            bufKey = Some(itemKey)
            return (currentKey, currentSeq)
          }
        }
        // we've reach the end of the list
        bufKey = None
        buf = None
        (currentKey, currentSeq)
      }
    }
  }
}
