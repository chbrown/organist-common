package org.anist

import java.io.{File, FileOutputStream, OutputStreamWriter}
import java.nio.charset.Charset

package object io {
  /**
    * Write or append a string to file with a specific encoding.
    *
    * @param file     Where to write contents
    * @param append   If true and the file exists, append to it instead of overwriting
    * @param encoding A valid Charset name used to encode string into bytes
    * @param contents Full string to write to file
    */
  def writeFile(file: File, append: Boolean, encoding: String)(contents: String): Unit = {
    // Avoid java.io.FileWriter, which doesn't allow specifying an encoding.
    // See https://stackoverflow.com/a/9853261
    val outputStream = new FileOutputStream(file, append)
    val utf8Encoder = Charset.forName("UTF-8").newEncoder()
    val streamWriter = new OutputStreamWriter(outputStream, utf8Encoder)
    streamWriter.write(contents)
    // OutputStreamWriter#close() calls flush() and close() on the underlying OutputStream
    streamWriter.close()
  }
  /**
    * Write or append a string to file with the UTF-8 encoding.
    */
  def writeFile(file: File, append: Boolean)(contents: String): Unit = {
    writeFile(file, append, "UTF-8")(contents)
  }
  /**
    * Write a string to file with the UTF-8 encoding.
    */
  def writeFile(file: File)(contents: String): Unit = {
    writeFile(file, false)(contents)
  }

  /**
    * Write or append a string to the file at the given pathname with a specific encoding.
    */
  def writeFile(pathname: String,
                append: Boolean,
                encoding: String)
               (contents: String): Unit = {
    writeFile(new File(pathname), append, encoding)(contents)
  }
  /**
    * Write or append a string to the file at the given pathname with the UTF-8 encoding.
    */
  def writeFile(pathname: String, append: Boolean)(contents: String): Unit = {
    writeFile(pathname, append, "UTF-8")(contents)
  }
  /**
    * Write a string to the file at the given pathname with the UTF-8 encoding.
    */
  def writeFile(pathname: String)(contents: String): Unit = {
    writeFile(pathname, false)(contents)
  }
}
