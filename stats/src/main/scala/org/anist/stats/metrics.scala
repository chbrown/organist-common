package org.anist.stats

import org.apache.commons.text.similarity.LevenshteinDistance

object metrics {
  /**
    * Compute the Euclidean norm of the given vector.
    */
  def euclideanNorm(xs: TraversableOnce[Double]): Double = {
    // TODO: type parameterize with Numeric[T]; first attempts to do so failed :(
    // References:
    // - http://stackoverflow.com/a/4058564
    // - http://stackoverflow.com/a/13670939
    math.sqrt(xs.map(x => math.pow(x, 2)).sum)
  }

  def cosineSimilarity(xs: Seq[Double], ys: Seq[Double]): Double = {
    // why not: a.zip(b).map(_*_).sum / (norm(a) * norm(b))
    // or even: a.zip(b).map((a, b) => a * b).sum / (norm(a) * norm(b))
    xs.zip(ys).map { case (x, y) => x * y }.sum / (euclideanNorm(xs) * euclideanNorm(ys))
  }

  /**
    * Find the Jaccard similarity (the size of the intersection divided by the size of the union) between two sets.
    *
    * @param a A set of elements of type T
    * @param b Another set of elements of type T (order of a and b does not matter)
    * @tparam T Can be any type, will be inferred from a and b, but a and b must have the same type.
    * @return A similarity measure between 0 and 1: 0 if the sets are completely discrete, 1 if they are identical, or somewhere in between; e.g., if they are the same length and share 2/3 of their elements, it will be 0.5
    */
  def jaccardSimilarity[T](a: Set[T], b: Set[T]): Double = {
    (a & b).size.toDouble / (a | b).size.toDouble
  }

  /**
    * Compute the Levenshtein similarity between two strings, using org.apache.commons.text.similarity.LevenshteinDistance
    *
    * @param a A string
    * @param b Another string (Levenshtein distance is symmetric, so order does not matter)
    * @return A distance measure between 0 and 1; 0 if the strings are identical (or if they are both empty), 1 if they are completely different, or somewhere in between; e.g., stringDistance("abcde", "abcdz") would be .2
    */
  def levenshteinDistance(a: String, b: String): Double = {
    val maxDistance = scala.math.max(a.length, b.length)
    if (maxDistance == 0) {
      0.0
    }
    else {
      LevenshteinDistance.getDefaultInstance.apply(a, b).toDouble / maxDistance.toDouble
    }
  }

  /**
    * Returns 1 if the strings are identical, down to 0 if they are completely different.
    * See levenshteinDistance(a, b) for details.
    */
  def levenshteinSimilarity(a: String, b: String): Double = {
    1.0 - levenshteinDistance(a, b)
  }
}
