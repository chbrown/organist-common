package org.anist.common

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
  * @param iterator Underlying iterator, such as the result of Source.getLines()
  * @param keyFn The discriminator function. As the input is grouped into sub-iterators
  *              such that for all items in the iterator, keyFn(item) returns the same value
  */
class LazyGroupByIterator[T, K](iterator: Iterator[T])(keyFn: T => K) extends Iterator[(K, Iterator[T])] {
  var cursor = iterator
  // don't touch iterator after this!
  def hasNext = cursor.hasNext
  def next(): (K, Iterator[T]) = {
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
