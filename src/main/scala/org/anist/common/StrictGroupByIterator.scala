package org.anist.common

import scala.collection.mutable

/**
  * Nested Iterators (see LazyGroupByIterator) are a mess. If we have lazy nested iterators,
  * what if we call next() on the outer iterator before the inner one? And in most cases, it's the
  * outer Iterator that's important.
  *
  * Initialization is non-lazy; iterator.next() will be called immediately to prepare the internal buffer
  *
  * @param iterator The underlying Iterator which we are grouping within.
  * @param keyFn The function that distinguishes between groups.
  * @tparam T The type of the elements in the underlying Iterator.
  * @tparam K The type of the keys derived from each element via keyFn.
  */
class StrictGroupByIterator[T, K](iterator: Iterator[T])(keyFn: T => K) extends Iterator[(K, Seq[T])] {
  // bufKey and buf should either be both null or both not-null
  // but since K might be a String, which cannot be null, we have to wrap in Options and use None instead of null
  private var buf: Option[T] = None
  private var bufKey: Option[K] = None
  if (iterator.hasNext) {
    val item = iterator.next()
    buf = Some(item)
    bufKey = Some(keyFn(item))
  }

  def hasNext = bufKey.nonEmpty

  def next(): (K, Seq[T]) = {
    // bufKey and buf should be set
    val currentKey = bufKey.get
    val currentSeq = new mutable.Queue[T]
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
