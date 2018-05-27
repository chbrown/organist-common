package org.anist.stats

object classification {
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
    * @param relevant  Set of documents that are actually relevant, based on the gold truth,
    *                  i.e., both true positives and false negatives (should have been retrieved but weren't)
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
      val truePositivesSize = (retrieved & relevant).size.toDouble
      val precision = if (retrieved.isEmpty) 0.0 else truePositivesSize / retrieved.size
      val recall = if (relevant.isEmpty) 0.0 else truePositivesSize / relevant.size
      (precision, recall)
    }
  }
}
