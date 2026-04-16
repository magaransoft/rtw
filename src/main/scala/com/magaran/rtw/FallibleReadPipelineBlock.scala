package com.magaran.rtw

/** A fallible pipeline block that executes within a read operation context. */
class FallibleReadPipelineBlock[A, B](inputValue: A)(block: A => B) extends FalliblePipelineBlock[B] {

  protected override def invokeBlock: B = block(inputValue)

  protected override val writeEnabled: Boolean = false

  protected override val readEnabled: Boolean = true

}
