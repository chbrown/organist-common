package org.anist.common

import java.text.Normalizer

object Text {
  /**
    * Stop-words derived from the Google 1T corpus.
    * 1) I started with just top 100 unigrams
    * 2) converted to lowercase and removed duplicates
    * 3) removed punctuation and numbers
    * 4) removed common nouns and pronouns
    */
  val stopwords = Set("a", "an", "and", "are", "as", "at", "be", "but", "by",
    "can", "do", "for", "from", "has", "have", "if", "in", "is", "it", "not",
    "of", "on", "one", "or", "out", "that", "the", "to", "up", "was", "which",
    "will", "with")

  val combiningDiacriticalMarksRegex = "\\p{InCombiningDiacriticalMarks}".r

  /**
    * Apply NFD (Canonical Decomposition) normalization via java.text.Normalizer,
    * and then remove all combining diacritical marks.
    */
  def normalize(string: String): String = {
    val nfdString = Normalizer.normalize(string, Normalizer.Form.NFD)
    combiningDiacriticalMarksRegex.replaceAllIn(nfdString, "")
  }

  val tokenRegex = "\\b\\w+\\b".r

  /**
    * Apply NFD normalization, remove diacritics, find all word-boundary bounded
    * sequences of word characters, and remove stop-words.
    */
  def tokenize(string: String): List[String] = {
    val normalizedString = normalize(string.toLowerCase)
    tokenRegex.findAllIn(normalizedString).toList.filterNot(stopwords)
  }
}
