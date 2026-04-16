package com.magaran.rtw

/** A preprocessor that may fail before the main pipeline block executes.
  * If preprocessing fails, the pipeline short-circuits with the error value.
  *
  * @tparam A the success type produced by preprocessing
  * @tparam B the error type produced on failure
  * @tparam C the context type required for preprocessing
  */
trait FalliblePreProcessor[A, B, C] {

  def tryPreProcessing(using C): Either[B, A]

  def writeEnabled: Boolean

  def readEnabled: Boolean

}
