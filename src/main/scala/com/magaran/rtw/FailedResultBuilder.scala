package com.magaran.rtw

import com.magaran.typedmap.TypedMap

/** Builds a failure result when a fallible pipeline block has failed.
  * The failure metadata (including the error value) is passed to construct the result.
  */
trait FailedResultBuilder[+A] {

  def buildFailure(metadata: TypedMap): A

}
