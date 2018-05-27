package org.anist.text

import java.io.{Closeable, File, InputStream}
import java.text.Normalizer

import org.apache.pdfbox

case class PDF(document: pdfbox.pdmodel.PDDocument) extends Closeable {
  /**
    * @param pageStart      Defaults to U+000C "FORM FEED (FF)"
    * @param paragraphStart Defaults to U+2029 "PARAGRAPH SEPARATOR"
    * @param lineSeparator  Defaults to U+000A "LINE FEED (LF)"
    * @return String representation of the text from the given PDF, using the default PDFTextStripper with
    */
  def stripText(pageStart: String = "\u000C",
                paragraphStart: String = "\u2029",
                lineSeparator: String = "\n"): String = {
    // TODO: maybe supply extra glyphlist?
    // > "To use your own glyphlist file, supply the file name to the glyphlist_ext JVM property."
    val stripper = new pdfbox.text.PDFTextStripper()
    stripper.setPageStart(pageStart)
    stripper.setParagraphStart(paragraphStart)
    stripper.setLineSeparator(lineSeparator)
    stripper.getText(document)
  }

  def close(): Unit = document.close()

  /**
    * Run PDFBox, repair hyphenation if selfRepair = true, and normalize.
    * @param pdf A wrapper around a PDFBox document
    * @param selfRepair Whether or not to use the language in this document
    *                   to automatically repair its hyphenation using heuristics.
    * @return A String with "\n" line breaks separating paragraphs,
    *         but no other weird whitespace or non-printing characters.
    */
  def extractText(selfRepair: Boolean) = {
    // Use PDFBox to extract the text with literal form feed characters to mark page breaks,
    // line feeds at each line break, and paragraph separator characters between paragraphs
    val text = stripText()
    // pdf.close()
    // TODO: repairHyphenation based on all text in corpus
    val repairedText = if (selfRepair) repairHyphenation(text) else text
    // Use Unicode normalization form NFKC
    // https://docs.oracle.com/javase/tutorial/i18n/text/normalizerapi.html
    val normalizedText = Normalizer.normalize(repairedText, Normalizer.Form.NFKC)
    // replace the paragraph and page placeholders with plain whitespace
    val plainWhitespaceText = normalizedText
      .replaceAllLiterally("\u2029", "\n")
      .replaceAllLiterally("\u000C", "\n\n")
    // https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
    // finally, and perhaps brutally,
    plainWhitespaceText
      // 1) replace all whitespace that is not " " or "\n" with a normal space
      //    Java's \p{Space} is defined as [ \t\n\x0B\f\r] (\x0B is vertical tab)
      .replaceAll("""[\t\x0B\f\r]""", " ")
      // 2) delete all characters that are not one of: graphical, space, or newline
      .replaceAll("""[^\p{Graph} \n]""", "")
      // and trim any remaining leading or trailing whitespace
      .trim
  }
}

object PDF {
  def load(file: File): PDF = {
    new PDF(pdfbox.pdmodel.PDDocument.load(file))
  }
  def load(input: InputStream): PDF = {
    new PDF(pdfbox.pdmodel.PDDocument.load(input))
  }
}
