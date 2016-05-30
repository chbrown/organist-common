package org.anist.common

import java.io.{File, FileOutputStream, OutputStreamWriter}

/**
  * The standard FileWriter sucks, see:
  * - http://stackoverflow.com/questions/9852978/write-a-file-in-utf-8-using-filewriter-java
  * - http://docs.oracle.com/javase/7/docs/api/java/io/FileWriter.html
  */
class FileWriter(pathname: String,
                 append: Boolean = false,
                 encoding: String = "UTF-8") {
  private val file = new File(pathname)
  private val fileOutputStream = new FileOutputStream(file, append)
  private val outputStreamWriter = new OutputStreamWriter(fileOutputStream, encoding)

  def write(output: String) {
    outputStreamWriter.write(output)
    outputStreamWriter.flush()
  }

  def close() {
    outputStreamWriter.close()
    fileOutputStream.close()
  }
}
