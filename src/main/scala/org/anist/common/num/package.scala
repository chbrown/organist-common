package org.anist.common

package object num {
  /** Boolean implicits */
  implicit class BooleanOps(val boolean: Boolean) extends AnyVal {
    /**
      * @return 1 if true, 0 if false
      */
    def toInt: Int = if (boolean) 1 else 0
    /**
      * @return 1.0 if true, 0.0 if false
      */
    def toDouble: Double = if (boolean) 1.0 else 0.0
  }
}
