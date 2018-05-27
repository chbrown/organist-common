package org.anist.common

import java.io.{ObjectInputStream, ObjectOutputStream}
import java.nio.file.{Files, Path}

object Persistence {
  def deserialize[T](path: Path): T = {
    val inputStream = new ObjectInputStream(Files.newInputStream(path))
    val obj = inputStream.readObject.asInstanceOf[T]
    inputStream.close()
    obj
  }

  def serialize[T](obj: T, path: Path): Unit = {
    val outputStream = new ObjectOutputStream(Files.newOutputStream(path))
    outputStream.writeObject(obj)
    outputStream.close()
  }
}
