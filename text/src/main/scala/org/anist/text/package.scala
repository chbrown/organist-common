package org.anist

import java.text.Normalizer

package object text {
  /**
    * Stop-words derived from the Google 1T corpus.
    * 1) start with just top 100 unigrams
    * 2) convert to lowercase and removed duplicates
    * 3) remove punctuation and numbers
    * 4) remove common nouns and pronouns
    */
  val stopwords = Set("a", "an", "and", "are", "as", "at", "be", "but", "by",
    "can", "do", "for", "from", "has", "have", "if", "in", "is", "it", "not",
    "of", "on", "one", "or", "out", "that", "the", "to", "up", "was", "which",
    "will", "with")

  /**
    * Apply Unicode normalization form NFD (Canonical Decomposition), remove all
    * combining diacritical marks, and re-combine with NFC (Canonical Composition).
    */
  def removeCombiningDiacriticalMarks(string: String): String = {
    val decomposedString = Normalizer.normalize(string, Normalizer.Form.NFD)
    val strippedString = """\p{InCombiningDiacriticalMarks}""".r.replaceAllIn(decomposedString, "")
    Normalizer.normalize(strippedString, Normalizer.Form.NFC)
  }

  /**
    * Tokenize `string` using a simple regular expression.
    *
    * @param string Raw string to tokenize (it will not be lower-cased)
    * @return Iterator over non-overlapping token strings
    */
  def tokenize(string: String): Iterator[String] = {
    """\b\w+\b""".r.findAllIn(string)
  }
}
