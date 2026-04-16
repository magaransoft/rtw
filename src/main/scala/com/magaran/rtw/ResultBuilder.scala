package com.magaran.rtw

/** Builds a final result from pipeline output and metadata context.
  * Implement this to define how pipeline results are assembled.
  */
trait ResultBuilder[-Input, Context, +Output] {

  def build(jsonObject: Input, context: Context): Output

}
