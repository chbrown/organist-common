package org.anist.common

import scala.collection.mutable

object ListHeap {
  /**
  ListHeap.merge implements the merge step after arriving at the lowest level of a
  mergesort, same as `heapq.merge(*lists)` in Python.

  Each item in `lists` should already be sorted.

  References:
  - http://stackoverflow.com/q/789250
  - http://docs.scala-lang.org/overviews/collections/performance-characteristics.html
  - http://lifelongprogrammer.blogspot.com/2014/12/scala-java-merge-k-sorted-list.html
  - http://stackoverflow.com/questions/5055909/algorithm-for-n-way-merge
  */
  def merge[T](lists: Iterable[List[T]])(implicit ev: T => Ordered[T]): Iterable[T] = {
    val headOrdering = new Ordering[List[T]] {
      def compare(a: List[T], b: List[T]) = b.head compare a.head
    }
    // create the PriorityQueue and add all the lists to it
    val pq = new mutable.PriorityQueue[List[T]]()(headOrdering)
    lists.filterNot(list => list.isEmpty).foreach(list => pq.enqueue(list))
    // pull things out of the PriorityQueue until it's exhausted
    val merged = mutable.ListBuffer[T]()
    while (pq.nonEmpty) {
      val top = pq.dequeue()
      merged += top.head
      if (top.tail.nonEmpty) {
        pq.enqueue(top.tail)
      }
    }
    merged
  }
}
