package com.magaran.rtw

/** A fallible pipeline block that executes within a write operation context. */
class FallibleWritePipelineBlock[A, B](inputValue: A)(block: A => B) extends FalliblePipelineBlock[B] {

  protected override def invokeBlock: B = block(inputValue)

  protected override val writeEnabled: Boolean = true

  protected override val readEnabled: Boolean = true

}
