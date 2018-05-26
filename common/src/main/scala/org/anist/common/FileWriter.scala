package org.anist.common

import java.io.{File, FileOutputStream, OutputStreamWriter}
import java.nio.charset.Charset

/**
  * The standard FileWriter sucks, see:
  * - https://stackoverflow.com/questions/9852978/write-a-file-in-utf-8-using-filewriter-java
  * - https://docs.oracle.com/javase/8/docs/api/java/io/FileWriter.html
  */
class FileWriter(pathname: String,
                 append: Boolean = false,
                 charsetName: String = "UTF-8") {
  private val file = new File(pathname)
  private val stream = new FileOutputStream(file, append)
  private val encoder = Charset.forName(charsetName).newEncoder()
  private val writer = new OutputStreamWriter(stream, encoder)

  def write(output: String) {
    writer.write(output)
    writer.flush()
  }

  def close() {
    writer.close()
    stream.close()
  }
}
