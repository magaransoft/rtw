package com.magaran.rtw

/** A pipeline block that executes within a write operation context. */
class WritePipelineBlock[A, B](inputValue: A)(block: A => B) extends PipelineBlock[B] {

  protected override def invokeBlock: B = block(inputValue)

  protected override val writeEnabled: Boolean = true

  protected override val readEnabled: Boolean = true
}
