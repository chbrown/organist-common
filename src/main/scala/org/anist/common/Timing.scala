package org.anist.common

object Timing {
  private def formatNanoseconds(ns: Long) = {
    if (ns > 60e9)
      f"${ns / 60e9}%.3fm"
    else if (ns > 1e9)
      f"${ns / 1e9}%.3fs"
    else if (ns > 1e6)
      f"${ns / 1e6}%.3fms"
    else if (ns > 1e3)
      f"${ns / 1e3}%.3fμs"
    else
      f"$ns%dns"
  }

  /**
    * Run the given block and print the time it took to finish to STDOUT.
    * @param fn A block to measure
    * @return The result of calling block
    */
  def time[R](fn: => R): R = {
    val started = System.nanoTime()
    val result = fn
    val elapsed_ns = System.nanoTime() - started
    println(s"Elapsed time: ${formatNanoseconds(elapsed_ns)}")
    result
  }
}
