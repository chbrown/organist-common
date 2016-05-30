package org.anist.common

/**
  * Prepare a Map from tokens to all the documents that contain them.
  *
  * @param documents List of (documentId, documentTokens) tuples
  */
class InvertedIndex(documents: Iterable[(String, Iterable[String])]) {
  /** Total number of documents (used to scale scores) */
  private val size = documents.size.toDouble
  /**
    * hashTable is the main data store:
    * Map[token: String -> matches: List(Tuple2(documentId: String, score: Double))]
  */
  private val hashTable = documents.flatMap { case (id, tokens) =>
    tokens.map(token => (token, id))
  } groupBy { case (token, id) =>
    token
  } mapValues { tokenIdTuples =>
    // The token part of each tuple in tokenIdTuples is identical and redundant,
    // since it's also in the key.
    val score = math.log(size / tokenIdTuples.size)
    // This score is like IDF
    // 1) If tokenIdTuples is small, indicating a rare token, the ratio will be
    //   very large, and so the log (score) will be quite larger
    // 2) If tokenIdTuples is large, indicating a common token, the ratio will
    //    approach 1 (but still be > 1), so the log (score) will be quite small
    // The resulting scores will range between 0 (the token occurs in all documents)
    // to log(documents.size) (the token occurs in a single document)
    tokenIdTuples.map { case (_, id) => (id, score) }.toList.sorted
  }

  /**
    * Find all documents that match some list of tokens, along with their scores.
    *
    * @param tokens A list of query tokens, generally a single document to find similar matches.
    * @return A list of documentIds and their scores, indicating similarity to the query tokens.
    *         Scores are scaled by the number of query tokens, so the resulting similarity
    *         scores have the same range as the single document match scores: from 0 to log(documents.size)
    */
  def search(tokens: Iterable[String]): Iterable[(String, Double)] = {
    val size = tokens.size.toDouble
    val idScores = tokens.map(token => hashTable.getOrElse(token, List()))
    val idScoreTuples = ListHeap.merge(idScores)

    Itertools.consecutiveGroupBy(idScoreTuples) { case (id, score) => id } map { case (id, tuples) =>
      (id, tuples.map { case (_, score) => score }.sum / size)
    }
  }
}
