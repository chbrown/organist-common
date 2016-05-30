package org.anist.common

object Math {
  /**
    * TODO: type parameterize with Numeric[T] (first attempts to do so failed)
    * References:
    * - http://stackoverflow.com/a/4058564
    * - http://stackoverflow.com/a/13670939
    */
  def euclideanNorm(xs: Traversable[Double]): Double = {
    math.sqrt(xs.map(x => math.pow(x, 2)).sum)
  }

  def cosineSimilarity(xs: Seq[Double], ys: Seq[Double]): Double = {
    // why not: a.zip(b).map(_*_).sum / (norm(a) * norm(b))
    // or even: a.zip(b).map((a, b) => a * b).sum / (norm(a) * norm(b))
    xs.zip(ys).map { case (x, y) => x * y }.sum / (euclideanNorm(xs) * euclideanNorm(ys))
  }

  /**
    * Find the Jaccard similarity (the size of the intersection divided by the
    * size of the union) between two sets.
    *
    * If the sets are identical, the similarity will be 1
    * If they are the same length and share 2/3 of their elements, it will be 0.5
    * If they are completely discrete, it will be 0
    */
  def jaccardSimilarity[T](source: Set[T], target: Set[T]): Double = {
    (source & target).size.toDouble / (source | target).size.toDouble
  }

  /**
    * First, let truePositives be the set of documents that were retrieved and were relevant:
    *   truePositives = relevant ∩ retrieved
    *
    * Precision is defined as: |truePositives| / |retrieved|
    *   I.e., what proportion of the retrieved documents were relevant?
    *
    * Recall is defined as: |truePositives| / |relevant|
    *   I.e., what proportion of the relevant documents were retrieved?
    *
    * In both cases, 1.0 is a "good" score, and 0.0 is a "bad" score.
    *
    * If the number of retrieved documents is 0, calculating the precision would be undefined.
    *   For the sake of usability, if both retrieved and relevant are 0, precision will be 1.0.
    *   Otherwise, if relevant > 0, precision will be 0.0
    *
    * Similarly, if the number of relevant documents is 0, calculating the recall would be undefined.
    *   If that's the case, but the number of retrieved documents is also 0, recall is 1.0.
    *   Otherwise, recall is 0.0.
    *
    * @param retrieved Set of documents predicted to be relevant by your prediction mechanism,
    *                  i.e., both true positives and false positives (should not have been retrieved but were)
    * @param relevant Set of documents that are actually relevant, based on the gold truth,
    *                 i.e., both true positives and false negatives (should have been retrieved but weren't)
    * @return Tuple of (precision, recall)
    */
  def measurePrecisionRecall[T](retrieved: Set[T], relevant: Set[T]): (Double, Double) = {
    // handle the special case of 0 documents retrieved and 0 documents recalled,
    // which doesn't require computing the intersection
    if (retrieved.isEmpty && relevant.isEmpty) {
      (1.0, 1.0)
    }
    else {
      // otherwise, at least one of precision / recall will require computing the intersection
      val truePositives = retrieved & relevant
      val precision = if (retrieved.isEmpty) 0.0 else truePositives.size / retrieved.size
      val recall = if (relevant.isEmpty) 0.0 else truePositives.size / relevant.size
      (precision, recall)
    }
  }
}
