package org.anist

import java.text.Normalizer
import scala.collection.immutable.TreeMap

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

  /**
    * Unwrap line breaks, replacing with a space or removing hyphens,
    * as determined by the word frequencies heuristics implemented in this function.
    *
    * @param text Text with literal line breaks and messy hyphenation
    * @param wordFrequency A bag of words used to identify intentionally hyphenated words.
    *                      It should probably be a SortedMap with an Ordering of String.CASE_INSENSITIVE_ORDER
    * @return A new String with all the linebreaks removed.
    */
  def repairHyphenation(text: String, wordFrequency: Map[String, Int]): String = {
    // look for all occurrences of "-\n", capturing the words before and after
    """(\w+)- *[\n\u2029]+(\w+)""".r.replaceAllIn(text, { hyphenatedBreakMatch =>
      // if line is hyphenated, and the word that is broken turns up in the corpus
      // more times WITH the hyphen than WITHOUT, return it WITH the hyphen
      val left = hyphenatedBreakMatch.group(1)
      val right = hyphenatedBreakMatch.group(2)
      val hyphenated = s"$left-$right"
      val joined = s"$left$right"
      val nHyphenated = wordFrequency.getOrElse(hyphenated, 0)
      val nJoined = wordFrequency.getOrElse(joined, 0)

      if (nHyphenated > nJoined) {
        hyphenated
      }
      else if (nJoined > nHyphenated) {
        joined
      }
      else {
        // otherwise, they're equal (both 0, usually), which is tougher

        // 1. if the second of the two parts is capitalized (Uppercase-Lowercase),
        //    it's probably a hyphenated name, so keep it hyphenated
        val capitalized = right.head.isUpper
        // 2. if the two parts are reasonable words in themselves, keep them
        //    hyphenated (it's probably something like "one-vs-all", or "bag-of-words")
        val leftCount = wordFrequency.getOrElse(left, 0)
        val rightCount = wordFrequency.getOrElse(right, 0)
        if (capitalized || (leftCount + rightCount) > 2) {
          hyphenated
        }
        else {
          // finally, default to dehyphenation, which is by far more common than
          // hyphenation (though it's more destructive of an assumption when wrong)
          joined
        }
      }
    }).replace('\n', ' ')
    // the remaining line breaks are legitimate breaks between words, so we simply
    // replace them with a plain SPACE
  }

  /**
    * Calls the full version, repairHyphenation(text, wordFrequency),
    * after building wordFrequency from the given text.
    *
    * @param text Text with literal line breaks and messy hyphenation
    * @return A new String with all the linebreaks removed.
    */
  def repairHyphenation(text: String): String = {
    /** Case-insensitive word counts across document */
    val wordFrequency = createCaseInsensitiveFrequencyMap("""\w+""".r.findAllIn(text))
    repairHyphenation(text, wordFrequency)
  }

  private val caseInsensitiveOrdering = Ordering.comparatorToOrdering(String.CASE_INSENSITIVE_ORDER)

  /**
    * Use TreeMap and String.CASE_INSENSITIVE_ORDER to create a mapping from words (tokens)
    * to counts, ignoring case.
    *
    * TODO: optimize initial Map creation by using some mutable Map structure, for speed.
    *       (will still need to return TreeMap for case-insensitive functionality)
    *
    * @param strings Pre-processed words or tokens (e.g., with "\\w+".r.findAllIn)
    * @return TreeMap with a case-insensitive ordering/comparator
    */
  private def createCaseInsensitiveFrequencyMap(strings: TraversableOnce[String]) = {
    strings.foldLeft(TreeMap.empty[String, Int](caseInsensitiveOrdering)) {
      case (map, token) => map + (token -> (map.getOrElse(token, 0) + 1))
    }
  }
}
