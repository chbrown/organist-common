package org.anist.stats

object random {
  /**
    * Sample from xs with replacement.
    *
    * @param xs   IndexedSeq (e.g., a Vector) of source elements
    * @param n    Number of source elements to sample
    * @param seed Random number generator seed (Random isn't serializable, so we can't reliably pass it in)
    * @return IndexedSeq[T] containing subset of elements from xs
    */
  def sample[T](xs: IndexedSeq[T], n: Int, seed: Long) = {
    val random = new scala.util.Random(seed)
    val max = xs.size
    IndexedSeq.fill(n) { max * random.nextDouble }.map(_.toInt).map(xs)
  }

  /**
    * Randomly split xs according to given weights.
    *
    * @param xs      Elements to be split
    * @param weights Proportional size of each class (will be normalized to sum to 1)
    * @param seed    Random seed (since Random instances aren't serializable)
    * @return A list of the same length as weights, containing lists that are partitions of xs
    */
  def split[T](xs: Seq[T], weights: Seq[Double], seed: Long) = {
    // Based on Apache Spark's org.apache.spark.rdd.RDD#randomSplit() (https://git.io/vhqbS)
    val random = new scala.util.Random(seed)
    val totalWeight = weights.sum
    val cumulativeWeights = weights.map(_ / totalWeight).scanLeft(0.0d)(_ + _)
    val cumulativeIndices = cumulativeWeights.map(_ * xs.size).map(_.toInt)
    val xsShuffled = random.shuffle(xs)
    cumulativeIndices.sliding(2).map {
      // case from :: until :: Nil => xs.slice(from, until) // why doesn't this type check?
      //bounds => xsShuffled.slice(bounds(0), bounds(1))
      case Seq(from, until) => xsShuffled.slice(from, until)
    }.toSeq
  }

  /**
    * Like randomSplit[T], but assumes a 2-class split and so returns a 2-tuple.
    */
  def trainTestSplit[T](xs: Seq[T], trainRatio: Double, seed: Long) = {
    val Seq(train, test) = split(xs, Seq(trainRatio, 1.0 - trainRatio), seed)
    (train, test)
  }
}
